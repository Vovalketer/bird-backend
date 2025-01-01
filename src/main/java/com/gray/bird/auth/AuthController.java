package com.gray.bird.auth;

import org.springframework.http.HttpStatus;
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

import com.gray.bird.auth.dto.LoginRequest;
import com.gray.bird.auth.dto.LoginResponse;
import com.gray.bird.common.HttpResponse;
import com.gray.bird.common.HttpUtils;
import com.gray.bird.common.ResourcePaths;
import com.gray.bird.exception.ApiException;
import com.gray.bird.user.UserService;

@RestController
@RequestMapping(path = ResourcePaths.AUTH)
@RequiredArgsConstructor
@Slf4j
public class AuthController {
	private final AuthService authService;
	private final UserService userService;

	@PostMapping("/reset-password")
	public ResponseEntity<HttpResponse<Void>> resetPassword() {
		throw new ApiException("Unimplemented");
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(
		@RequestBody @Valid LoginRequest login, HttpServletRequest request, HttpServletResponse response) {
		LoginResponse cookies = authService.login(login);
		response.addCookie(cookies.accessToken());
		response.addCookie(cookies.refreshToken());
		return ResponseEntity.ok(null);
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<?> getNewAccessToken(HttpServletRequest request, HttpServletResponse response) {
		log.info("within refresh function");
		Cookie[] cookies = request.getCookies();
		Cookie accessToken = authService.refreshAccessToken(cookies);
		response.addCookie(accessToken);

		return ResponseEntity.ok().body(
			HttpUtils.getResponse(request, "Access token refreshed successfully", HttpStatus.OK));
	}

	@GetMapping("/verify/account")
	public ResponseEntity<?> verifyAccount(@RequestParam("token") String token, HttpServletRequest request) {
		userService.validateAccount(token);
		return ResponseEntity.ok(HttpUtils.getResponse(request, "Account verified", HttpStatus.OK));
	}
}
