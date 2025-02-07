package com.gray.bird.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.Cookie;

import java.nio.CharBuffer;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.gray.bird.auth.dto.LoginRequest;
import com.gray.bird.auth.dto.LoginResponse;
import com.gray.bird.auth.event.AuthEventPublisher;
import com.gray.bird.auth.jwt.JwtService;
import com.gray.bird.auth.jwt.TokenType;
import com.gray.bird.common.HttpUtils;
import com.gray.bird.exception.ApiException;
import com.gray.bird.exception.ExpiredJwtException;
import com.gray.bird.exception.InvalidJwtException;
import com.gray.bird.exception.UnauthorizedException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
	@Value("${jwt.access-token-expiration}")
	private Integer JWT_ACCESS_TOKEN_EXPIRATION;
	@Value("${jwt.refresh-token-expiration}")
	private Integer JWT_REFRESH_TOKEN_EXPIRATION;
	private String JWT_REFRESH_PATH = "/api/auth/refresh";
	private final JwtService jwtService;
	private final AuthCache authCache;
	private final RefreshTokenRepository refreshTokenRepository;
	private final BCryptPasswordEncoder encoder;
	private final UserPrincipalService userPrincipalService;
	private final AuthEventPublisher publisher;

	public String refreshAccessToken(Cookie[] cookies) {
		Optional<Cookie> cookie = HttpUtils.extractCookie(cookies, TokenType.REFRESH.getValue());
		if (cookie.isEmpty()) {
			throw new UnauthorizedException();
		}
		String token = cookie.get().getValue();

		if (validateRefreshToken(token)) {
			String userId = jwtService.getSubject(token);
			UserPrincipal user = userPrincipalService.loadUserByUuid(UUID.fromString(userId));
			return jwtService.createAccessToken(user, JWT_ACCESS_TOKEN_EXPIRATION);
		}
		throw new InvalidJwtException();
	}

	public LoginResponse login(LoginRequest data) {
		UserPrincipal user = userPrincipalService.loadUserByEmail(data.email());
		log.info("Logging user authorities upon login");
		log.info(user.getAuthorities().toString());
		if (isAccountValid(user)) {
			handleLoginAttempts(user, LoginType.LOGIN_ATTEMPT);
			log.info("valid account");
			log.info("submitted pw: {}", data.password());
			if (encoder.matches(CharBuffer.wrap(data.password()), user.getPassword())) {
				log.info("acc/pass match, proceeding...");
				handleLoginAttempts(user, LoginType.LOGIN_SUCCESS);
				String accessToken = jwtService.createAccessToken(user, JWT_ACCESS_TOKEN_EXPIRATION);
				String refreshToken = jwtService.createRefreshToken(user, JWT_REFRESH_TOKEN_EXPIRATION);

				Cookie refreshCookie = new Cookie(TokenType.REFRESH.getValue(), refreshToken);
				refreshCookie.setHttpOnly(true);
				refreshCookie.setAttribute("SameSite", SameSite.NONE.name());
				// TODO: implement https support
				// cookie.setSecure(true);
				refreshCookie.setMaxAge(JWT_REFRESH_TOKEN_EXPIRATION);
				refreshCookie.setPath(JWT_REFRESH_PATH);
				publisher.publishUserLoggedInEvent(UUID.fromString(user.getUsername()));

				return new LoginResponse(accessToken, refreshCookie);

			} else {
				throw new BadCredentialsException("Incorrect email/password");
			}
		}

		throw new ApiException("Your account is disabled"); // shouldnt reach this point
	}

	// could also grab the IP and introduce some IP restrictions,
	// logging and possibly sending an email
	private void handleLoginAttempts(UserPrincipal data, LoginType type) {
		String identifier = data.getUsername();
		Integer attempts = authCache.getLoginAttempts(identifier);
		log.info("Current login attempts: " + attempts);
		switch (type) {
			case LOGIN_ATTEMPT -> {
				if (attempts > AuthConstants.MAX_LOGIN_ATTEMPTS) {
					throw new DisabledException("Too many login attempts, your account is "
						+ "temporarily disabled. Please try again later");
					// TODO: define whether the account should be locked permanently until the user
					// unlocks it after several attempts or not
					// send an email notifying about the issue if it gets locked
				}
				authCache.incrementLoginAttempts(identifier);
			}
			case LOGIN_SUCCESS -> {
				authCache.clearLoginAttempts(identifier);
				log.info("User {} has logged in successfully", data.getUsername());
			}
		}
	}

	// TODO: revoke refresh token

	private boolean isAccountValid(UserPrincipal user) {
		log.info(user.toString());
		if (!user.isEnabled()) {
			log.info("disabled?");
			throw new DisabledException("Your account is currently disabled");
		}
		if (!user.isAccountNonLocked()) {
			log.info("locked?");
			throw new LockedException("Your account is currently locked");
		}
		if (!user.isCredentialsNonExpired()) {
			log.info("cred expired?");
			throw new CredentialsExpiredException("Your password has expired. Please update your password");
		}
		if (!user.isAccountNonExpired()) {
			log.info("expired?");
			throw new DisabledException("Your account has expired. Please contact an administrator");
		}
		log.info("end of validation, success");
		return true;
	}

	private boolean validateRefreshToken(String refreshToken) {
		if (jwtService.validateToken(refreshToken)) {
			RefreshTokenEntity refresh =
				refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> new InvalidJwtException());
			if (refresh.getExpiresAt().isBefore(LocalDateTime.now()) || refresh.getRevokedAt() != null) {
				throw new ExpiredJwtException();
			};
			if (!Objects.equals(jwtService.getSubject(refreshToken), refresh.getUserId().toString())) {
				throw new InvalidJwtException();
			}
			return true;
		}
		throw new InvalidJwtException();
	}
}
