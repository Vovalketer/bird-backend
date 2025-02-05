package com.gray.bird.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

import com.gray.bird.auth.dto.AccessToken;
import com.gray.bird.auth.dto.LoginRequest;
import com.gray.bird.auth.dto.LoginResponse;
import com.gray.bird.auth.jwt.TokenType;
import com.gray.bird.common.JsonApiResponse;
import com.gray.bird.common.utils.JsonApiResponseFactory;
import com.gray.bird.user.registration.AccountVerificationService;

@ExtendWith(SpringExtension.class)
public class AuthControllerTest {
	private static final String REFRESH_TOKEN = "refresh_token";

	@Mock
	private AuthService authService;
	@Mock
	private AccountVerificationService accountVerificationService;
	@Mock
	private JsonApiResponseFactory responseFactory;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	@InjectMocks
	private AuthController authController;

	@BeforeEach
	void setUp() {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	@Test
	void login() throws Exception {
		LoginRequest data = new LoginRequest("fake1@email.com", "testpassword");
		LoginResponse tokens = new LoginResponse("mockvalue", new Cookie(REFRESH_TOKEN, "mockvalue"));

		Mockito.when(authService.login(Mockito.any(LoginRequest.class))).thenReturn(tokens);

		ResponseEntity<AccessToken> login = authController.login(data, request, response);

		Assertions.assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(login.getBody()).isNotNull();
		Assertions.assertThat(login.getBody().accessToken()).isEqualTo(tokens.accessToken());
		Assertions.assertThat(response.getCookie(TokenType.REFRESH.getValue()))
			.isEqualTo(tokens.refreshToken());

		Mockito.verify(authService).login(data);
	}

	@Test
	void getNewAccessTokenWithValidRefreshToken() throws Exception {
		String accessTok = "mockAccessToken";
		Mockito.when(authService.refreshAccessToken(Mockito.any())).thenReturn(accessTok);

		HttpServletRequest servRequest = Mockito.mock(HttpServletRequest.class);
		Cookie[] cookies = new Cookie[] {new Cookie(REFRESH_TOKEN, "mockRefreshToken")};
		Mockito.when(servRequest.getCookies()).thenReturn(cookies);

		ResponseEntity<AccessToken> accessToken = authController.getNewAccessToken(servRequest);

		Assertions.assertThat(accessToken.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(accessToken.getBody()).isNotNull();
		Assertions.assertThat(accessToken.getBody().accessToken()).isEqualTo(accessTok);
		Mockito.verify(authService).refreshAccessToken(cookies);
	}

	@Test
	void verifyAccountValid() throws Exception {
		String token = "valid token";
		Mockito.doNothing().when(accountVerificationService).verifyAccount(token);

		JsonApiResponse<Object> res = new JsonApiResponse<>(null);
		Mockito.when(responseFactory.createResponse(null)).thenReturn(res);
		ResponseEntity<JsonApiResponse<Void>> verifyAccount = authController.verifyAccount(token);

		Assertions.assertThat(verifyAccount.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(verifyAccount.getBody()).isNotNull();
		Assertions.assertThat(verifyAccount.getBody().getMetadata().getMetadata("message"))
			.isEqualTo(Optional.of("Account verified"));
	}
}
