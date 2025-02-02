package com.gray.bird.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.Collections;

import com.gray.bird.auth.dto.LoginRequest;
import com.gray.bird.auth.dto.LoginResponse;
import com.gray.bird.auth.jwt.TokenType;
import com.gray.bird.common.JsonApiResponse;
import com.gray.bird.common.ResourcePaths;
import com.gray.bird.common.utils.JsonApiResponseFactory;
import com.gray.bird.exception.ApiException;
import com.gray.bird.user.registration.AccountVerificationService;

@RestController
@RequestMapping(path = ResourcePaths.AUTH)
@RequiredArgsConstructor
@Slf4j
public class AuthController {
	private final AuthService authService;
	private final AccountVerificationService accountVerificationService;
	private final JsonApiResponseFactory responseFactory;

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword() {
		throw new ApiException("Unimplemented");
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(
		@RequestBody @Valid LoginRequest login, HttpServletRequest request, HttpServletResponse response) {
		LoginResponse tokens = authService.login(login);
		response.addCookie(tokens.refreshToken());
		return ResponseEntity.ok(Collections.singletonMap(TokenType.ACCESS.getValue(), tokens.accessToken()));
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<?> getNewAccessToken(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		String accessToken = authService.refreshAccessToken(cookies);

		return ResponseEntity.ok(Collections.singletonMap(TokenType.ACCESS.getValue(), accessToken));
	}

	@GetMapping("/verify/account")
	public ResponseEntity<?> verifyAccount(@RequestParam("token") String token, HttpServletRequest request) {
		accountVerificationService.verifyAccount(token);
		JsonApiResponse<Object> response = responseFactory.createResponse(null);
		response.addMetadata("message", "Account verified");
		return ResponseEntity.ok(response);
	}
}
