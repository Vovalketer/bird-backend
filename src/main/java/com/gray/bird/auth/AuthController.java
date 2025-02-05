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

import com.gray.bird.auth.dto.AccessToken;
import com.gray.bird.auth.dto.LoginRequest;
import com.gray.bird.auth.dto.LoginResponse;
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
	public ResponseEntity<AccessToken> login(
		@RequestBody @Valid LoginRequest login, HttpServletRequest request, HttpServletResponse response) {
		LoginResponse tokens = authService.login(login);
		response.addCookie(tokens.refreshToken());
		return ResponseEntity.ok(new AccessToken(tokens.accessToken()));
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<AccessToken> getNewAccessToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		String accessToken = authService.refreshAccessToken(cookies);
		return ResponseEntity.ok(new AccessToken(accessToken));
	}

	@GetMapping("/verify/account")
	public ResponseEntity<JsonApiResponse<Void>> verifyAccount(@RequestParam("token") String token) {
		accountVerificationService.verifyAccount(token);
		JsonApiResponse<Void> response = responseFactory.createResponse(null);
		response.addMetadata("message", "Account verified");
		return ResponseEntity.ok(response);
	}
}
