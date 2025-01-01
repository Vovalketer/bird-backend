package com.gray.bird.security;

import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

import com.gray.bird.auth.jwt.JwtService;
import com.gray.bird.auth.jwt.TokenType;
import com.gray.bird.common.HttpUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
	private final JwtService jwtService;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request,
		@NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
		throws ServletException, IOException {
		log.info("FILTER CHAIN START");
		Optional<Cookie> cookie =
			HttpUtils.extractCookie(request.getCookies(), TokenType.ACCESS.getValue());
		if (cookie.isPresent()) {
			log.info("JWT VALIDATION START");
			boolean validToken = jwtService.validateToken(cookie.get().getValue());
			if (validToken) {
				log.info("Cookie containing token found, value: {}", cookie.get().getValue());
				SecurityContextHolder.getContext().setAuthentication(
					jwtService.getAuthenticationFromAccessToken(cookie.get().getValue()));
			} else {
				log.info("Invalid token");
				SecurityContextHolder.clearContext();
			}
		} else {
			log.info("No valid token found");
			SecurityContextHolder.clearContext();
		}

		filterChain.doFilter(request, response);
	}
}
