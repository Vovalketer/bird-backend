package com.gray.bird.testConfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.gray.bird.auth.jwt.JwtService;
import com.gray.bird.security.JwtFilter;
import com.gray.bird.security.SecurityConfig;
import com.gray.bird.security.UserAuthEntryPoint;

@TestConfiguration
@Import(SecurityConfig.class)
public class SecurityTestConfig {
	@Bean
	public UserAuthEntryPoint userAuthEntryPoint() {
		return new UserAuthEntryPoint();
	}

	@Bean
	public JwtService jwtService() {
		return new JwtService();
	}

	@Bean
	public JwtFilter jwtFilter(JwtService jwtService) {
		return new JwtFilter(jwtService);
	}
}
