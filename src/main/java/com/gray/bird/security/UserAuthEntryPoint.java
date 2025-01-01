package com.gray.bird.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gray.bird.common.HttpUtils;

@Component
public class UserAuthEntryPoint implements AuthenticationEntryPoint {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		OBJECT_MAPPER.writeValue(response.getOutputStream(),
			HttpUtils.getResponse(request,
				Collections.EMPTY_MAP,
				authException.getMessage(),
				HttpStatus.UNAUTHORIZED));
	}
}
