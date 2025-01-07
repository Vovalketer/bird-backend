package com.gray.bird.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	private final UserAuthEntryPoint userAuthEntryPoint;
	private final JwtFilter jwtFilter;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(Customizer.withDefaults())
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(req
				-> req.requestMatchers("/api/auth/**", "/api/users/register")
					.permitAll()
					.requestMatchers(HttpMethod.GET, "/api/posts/**")
					.permitAll()
					.requestMatchers(HttpMethod.GET, "/api/users/**")
					.permitAll()
					.anyRequest()
					.authenticated())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.exceptionHandling(
				exceptionHandling -> exceptionHandling.authenticationEntryPoint(userAuthEntryPoint))
			// .authenticationProvider(userAuthenticationProvider)
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	static RoleHierarchy roleHierarchy() {
		return RoleHierarchyImpl.withDefaultRolePrefix()
			.role("SUPER_ADMIN")
			.implies("ADMIN")
			.role("ADMIN")
			.implies("STAFF")
			.role("STAFF")
			.implies("USER")
			.role("USER")
			.implies("GUEST")
			.build();
	}

	// Adds /api just after the server address, ie: myserver.com/api/my/controller
}
