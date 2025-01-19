package com.gray.bird.auth;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gray.bird.auth.dto.LoginRequest;
import com.gray.bird.auth.dto.LoginResponse;
import com.gray.bird.common.ResourcePaths;
import com.gray.bird.exception.GlobalExceptionHandler;
import com.gray.bird.exception.InvalidConfirmationTokenException;
import com.gray.bird.exception.InvalidJwtException;
import com.gray.bird.user.UserService;
import com.gray.bird.user.registration.AccountVerificationService;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class,
	useDefaultFilters = false)
@ContextConfiguration(classes = AuthController.class)
@Import(GlobalExceptionHandler.class)
public class AuthControllerTest {
	private static final String REFRESH_TOKEN = "refresh_token";
	private static final String AUTH_ENDPOINT = ResourcePaths.AUTH;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private AuthService authService;
	@MockitoBean
	private UserService userService;
	@MockitoBean
	private AccountVerificationService accountVerificationService;
	@MockitoBean
	private HttpServletRequest request;
	@MockitoBean
	private HttpServletResponse response;

	@Test
	void login() throws Exception {
		LoginRequest data = new LoginRequest("fake1@email.com", "testpassword");
		LoginResponse tokens = new LoginResponse("mockvalue", new Cookie(REFRESH_TOKEN, "mockvalue"));

		Mockito.when(authService.login(ArgumentMatchers.any(LoginRequest.class))).thenReturn(tokens);

		mockMvc
			.perform(MockMvcRequestBuilders
					.post(AUTH_ENDPOINT + "/login")
					// .perform(MockMvcRequestBuilders.post(uri)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(data)))
			// .with(SecurityMockMvcRequestPostProcessors.csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.access_token").exists())
			.andExpect(jsonPath("$.access_token").value(tokens.accessToken()))
			.andExpect(cookie().exists(REFRESH_TOKEN));
	}

	@Test
	void getNewAccessTokenWithValidRefreshToken() throws Exception {
		Cookie refreshTok = new Cookie(REFRESH_TOKEN, "mockRefreshToken");
		String accessTok = "mockAccessToken";

		Mockito.when(authService.refreshAccessToken(ArgumentMatchers.any(Cookie[].class)))
			.thenReturn(accessTok);

		mockMvc.perform(MockMvcRequestBuilders.post(AUTH_ENDPOINT + "/refresh-token").cookie(refreshTok))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.access_token").exists())
			.andExpect(jsonPath("$.access_token").value(accessTok));
	}

	@Test
	void anInvalidRefreshTokenShouldThrowWhenTryingToRefresh() throws Exception {
		Cookie refresh = new Cookie(REFRESH_TOKEN, "INVALIDTOKEN");
		Cookie[] cookies = {refresh};

		Mockito.when(authService.refreshAccessToken(ArgumentMatchers.any(Cookie[].class)))
			.thenThrow(new InvalidJwtException("Invalid token"));

		mockMvc.perform(MockMvcRequestBuilders.post(AUTH_ENDPOINT + "/refresh-token").cookie(cookies))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void verifyAccountValid() throws Exception {
		String token = "valid token";
		Mockito.doNothing().when(accountVerificationService).verifyAccount(ArgumentMatchers.anyString());

		mockMvc.perform(MockMvcRequestBuilders.get(AUTH_ENDPOINT + "/verify/account").param("token", token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", Matchers.containsString("Account verified")));
	}

	@Test
	void invalidTokenVerifyAccount() throws Exception {
		String token = "invalid token";
		Mockito.doThrow(new InvalidConfirmationTokenException())
			.when(accountVerificationService)
			.verifyAccount(token);

		mockMvc.perform(MockMvcRequestBuilders.get(AUTH_ENDPOINT + "/verify/account").param("token", token))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message", Matchers.containsString("invalid")));
	}
}
