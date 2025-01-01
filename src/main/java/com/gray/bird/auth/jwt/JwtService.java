package com.gray.bird.auth.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.Cookie;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

import com.gray.bird.auth.UserPrincipal;
import com.gray.bird.exception.ExpiredJwtException;
import com.gray.bird.exception.InvalidJwtException;
import com.gray.bird.role.RoleConstants;
import com.gray.bird.role.RoleType;
import com.gray.bird.security.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
	@Value("${jwt.access-token-expiration}")
	private Integer JWT_ACCESS_TOKEN_EXPIRATION;
	@Value("${jwt.refresh-token-expiration}")
	private Integer JWT_REFRESH_TOKEN_EXPIRATION;
	private String ACCESS_TOKEN_PATH = "/";
	private String REFRESH_TOKEN_PATH = "/auth";
	@Value("${jwt.issuer")
	private String ISSUER;
	@Value("${jwt.secret}")
	private String JWT_SECRET;

	private final RefreshTokenRepository refreshTokenRepository;

	public String createToken(UserPrincipal user, TokenType type) {
		// String tokenId = new AlternativeJdkIdGenerator().generateId().toString();
		String token = buildToken.apply(user, type);
		if (type == TokenType.REFRESH) {
			RefreshTokenEntity refresh = new RefreshTokenEntity(
				token, user.getUsername(), LocalDateTime.now().plusSeconds(JWT_REFRESH_TOKEN_EXPIRATION));
			refreshTokenRepository.save(refresh);
		}
		return token;
	}

	public Cookie createJwtCookie(UserPrincipal user, TokenType type) {
		String token = createToken(user, type);
		Cookie cookie = new Cookie(type.getValue(), token);
		cookie.setHttpOnly(true);
		cookie.setAttribute("SameSite", SameSite.NONE.name());
		// TODO: implement https support
		// cookie.setSecure(true);
		switch (type) {
			case ACCESS -> {
				cookie.setMaxAge(JWT_ACCESS_TOKEN_EXPIRATION);
				cookie.setPath(ACCESS_TOKEN_PATH);
			}
			case REFRESH -> {
				cookie.setMaxAge(JWT_REFRESH_TOKEN_EXPIRATION);
				cookie.setPath(REFRESH_TOKEN_PATH);
			}
		}
		return cookie;
	}

	public boolean validateToken(String token) {
		// the claims function will throw an exception if its invalid
		if (getClaimsValue(token, Claims::getExpiration).before(new Date())) {
			throw new ExpiredJwtException();
		}
		return true;
	}

	public boolean validateRefreshToken(String refreshToken) {
		Claims claims = claimsFunction.apply(refreshToken);
		RefreshTokenEntity refresh =
			refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> new InvalidJwtException());
		if (refresh.getExpiresAt().isBefore(LocalDateTime.now()) || refresh.getRevokedAt() != null) {
			log.info("expired here");
			throw new ExpiredJwtException();
		};
		if (!Objects.equals(claims.getSubject(), refresh.getUsername())) {
			log.info("claims subject: {} \nrefresh username: {}", claims.getSubject(), refresh.getUsername());
			throw new InvalidJwtException();
		};
		return true;
	}

	public TokenData getDataFromToken(String token) {
		Claims claims = claimsFunction.apply(token);
		String roleString = (String) claims.get(RoleConstants.ROLE);
		return new TokenData(claims.getSubject(), claims.getAudience(), RoleType.getType(roleString));
	}

	public Authentication getAuthenticationFromAccessToken(String token) {
		Claims claims = claimsFunction.apply(token);
		// WARNING
		List<String> role = claims.get(RoleConstants.ROLE, List.class);
		String username = claims.getSubject();
		Set<SimpleGrantedAuthority> roleSet =
			role.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
		// RoleType roleValue = RoleType.getType(role);
		// Collections.singleton(roleValue);
		log.info("Claims:{ \nusername: {} \nrole: {}\n}", username, role);

		return new UsernamePasswordAuthenticationToken(username, null, roleSet);
	}

	private final Supplier<SecretKey> key = () -> Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));

	private final Function<String, Claims> claimsFunction = token -> {
		try {
			return Jwts.parser().verifyWith(key.get()).build().parseSignedClaims(token).getPayload();
		} catch (Exception e) {
			throw new InvalidJwtException();
		}
	};

	private final <T> T getClaimsValue(String token, Function<Claims, T> claims) {
		return claimsFunction.andThen(claims).apply(token);
	}

	// public final Function<String, String> subject =
	// token -> getClaimsValue(token, Claims::getSubject);

	private final BiFunction<UserPrincipal, TokenType, String> buildToken = (user, tokenType) -> {
		String tok = null;
		Date currentTime = new Date();
		JwtBuilder builder = Jwts.builder()
								 .header()
								 .add(Map.of(SecurityConstants.TYPE, SecurityConstants.JWT_TYPE))
								 .and()
								 .audience()
								 .add("BirdApp")
								 .and()
								 // .id(tokenId)
								 .issuedAt(currentTime)
								 .notBefore(currentTime)
								 .signWith(key.get(), Jwts.SIG.HS512);
		switch (tokenType) {
			case TokenType.ACCESS -> {
				tok = builder.subject(user.getUsername())
						  .claim(RoleConstants.ROLE, user.getAuthorities())
						  .expiration(Date.from(Instant.now().plusSeconds(JWT_ACCESS_TOKEN_EXPIRATION)))
						  .compact();
			}
			case TokenType.REFRESH -> {
				tok = builder.subject(user.getUsername())
						  .expiration(Date.from(Instant.now().plusSeconds(JWT_REFRESH_TOKEN_EXPIRATION)))
						  .compact();
			}
		}
		return tok;
	};
}
