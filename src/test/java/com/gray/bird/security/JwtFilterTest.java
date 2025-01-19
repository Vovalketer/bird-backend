package com.gray.bird.security;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;

import com.gray.bird.auth.jwt.JwtService;

@ExtendWith(SpringExtension.class)
public class JwtFilterTest {
	@Mock
	JwtService jwtService;
	@Mock
	HttpServletRequest request;
	@Mock
	HttpServletResponse response;
	@Mock
	FilterChain filterChain;
	@InjectMocks
	JwtFilter jwtFilter;

	@Test
	void testDoFilterInternal() throws ServletException, IOException {
		String bearerToken = "Bearer "
			+ "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9."
			+ "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ."
			+ "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
		String resolvedToken = bearerToken.substring(7);
		Authentication authentication = new TestingAuthenticationToken("test", "test", "USER");
		Mockito.when(request.getHeader("Authorization")).thenReturn(bearerToken);
		Mockito.when(jwtService.validateToken(resolvedToken)).thenReturn(true);
		Mockito.when(jwtService.getAuthenticationFromAccessToken(resolvedToken)).thenReturn(authentication);
		jwtFilter.doFilterInternal(request, response, filterChain);

		Mockito.verify(jwtService).validateToken(resolvedToken);
		Mockito.verify(jwtService).getAuthenticationFromAccessToken(resolvedToken);
		Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication())
			.isEqualTo(authentication);
		Mockito.verify(filterChain).doFilter(request, response);
	}
}
