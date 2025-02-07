package com.gray.bird.auth.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.function.TriFunction;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
	@Value("${jwt.issuer")
	private String ISSUER;
	@Value("${jwt.secret}")
	private String JWT_SECRET;

	public String createAccessToken(UserPrincipal user, Integer expirationSeconds) {
		return buildToken.apply(user, expirationSeconds, TokenType.ACCESS);
	}

	public String createRefreshToken(UserPrincipal user, Integer expirationSeconds) {
		return buildToken.apply(user, expirationSeconds, TokenType.REFRESH);
	}

	public boolean validateToken(String token) {
		// the claims function will throw an exception if its invalid
		if (getClaimsValue(token, Claims::getExpiration).before(new Date())) {
			throw new ExpiredJwtException();
		}
		return true;
	}

	public Authentication getAuthenticationFromAccessToken(String token) {
		Claims claims = claimsFunction.apply(token);
		// WARNING
		List<String> role = claims.get(RoleConstants.ROLE, List.class);
		String userId = claims.getSubject();
		UUID userUuid = UUID.fromString(userId);
		Set<SimpleGrantedAuthority> roleSet =
			role.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());

		return new UsernamePasswordAuthenticationToken(userUuid, null, roleSet);
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

	public String getSubject(String token) {
		return getClaimsValue(token, Claims::getSubject);
	}

	private final TriFunction<UserPrincipal, Integer, TokenType, String> buildToken =
		(user, expirationSeconds, tokenType) -> {
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
						  .expiration(Date.from(Instant.now().plusSeconds(expirationSeconds)))
						  .compact();
			}
			case TokenType.REFRESH -> {
				tok = builder.subject(user.getUsername())
						  .expiration(Date.from(Instant.now().plusSeconds(expirationSeconds)))
						  .compact();
			}
		}
		return tok;
	};
}
