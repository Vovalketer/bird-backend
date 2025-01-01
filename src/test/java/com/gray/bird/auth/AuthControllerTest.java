package com.gray.bird.auth;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
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
import com.gray.bird.exception.GlobalExceptionHandler;
import com.gray.bird.exception.InvalidConfirmationTokenException;
import com.gray.bird.exception.InvalidJwtException;
import com.gray.bird.user.UserService;
import com.gray.bird.user.dto.RegisterRequest;
import com.gray.bird.user.dto.UserProjection;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class,
	useDefaultFilters = false)
@ContextConfiguration(classes = AuthController.class)
@Import(GlobalExceptionHandler.class)
public class AuthControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private AuthService authService;
	@MockBean
	private UserService userService;
	@MockBean
	private HttpServletRequest request;
	@MockBean
	private HttpServletResponse response;

	@Test
	void login() throws Exception {
		LoginRequest data = new LoginRequest("fake1@email.com", "testpassword");
		LoginResponse cookies = new LoginResponse(
			new Cookie("access-token", "mockvalue"), new Cookie("refresh-token", "mockvalue"));

		Mockito.when(authService.login(ArgumentMatchers.any(LoginRequest.class))).thenReturn(cookies);

		mockMvc
			.perform(MockMvcRequestBuilders
					.post("/auth/login")
					// .perform(MockMvcRequestBuilders.post(uri)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(data)))
			// .with(SecurityMockMvcRequestPostProcessors.csrf()))
			.andExpect(status().isOk())
			.andExpect(cookie().exists("access-token"))
			.andExpect(cookie().exists("refresh-token"))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.message", Matchers.containsString("Logged in")))
			.andReturn();
	}

	@Test
	void getNewAccessTokenWithValidRefreshToken() throws Exception {
		Cookie refreshTok = new Cookie("refresh-token", "mockRefreshToken");
		Cookie accessTok = new Cookie("access-token", "mockAccessToken");

		Mockito.when(authService.refreshAccessToken(ArgumentMatchers.any(Cookie[].class)))
			.thenReturn(accessTok);

		mockMvc.perform(MockMvcRequestBuilders.post("/auth/refresh-token").cookie(refreshTok))
			.andExpect(status().isOk())
			.andExpect(cookie().exists("access-token"));
	}

	@Test
	void anInvalidRefreshTokenShouldThrowWhenTryingToRefresh() throws Exception {
		Cookie refresh = new Cookie("refresh-token", "INVALIDTOKEN");
		Cookie[] cookies = {refresh};

		Mockito.when(authService.refreshAccessToken(ArgumentMatchers.any(Cookie[].class)))
			.thenThrow(new InvalidJwtException("Invalid token"));

		mockMvc.perform(MockMvcRequestBuilders.post("/auth/refresh-token").cookie(cookies))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void registerValidData() throws Exception {
		RegisterRequest data = new RegisterRequest("username", "some@email.com", "securepassword", "handle");
		UserProjection user =
			UserProjection.builder().username(data.username()).handle(data.handle()).build();
		Mockito.when(userService.createUser(data)).thenReturn(user);
		mockMvc
			.perform(MockMvcRequestBuilders.post("/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(data)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.message", Matchers.containsString("Account created")))
			.andExpect(jsonPath("$.data.username").value(user.username()))
			.andExpect(jsonPath("$.data.handle").value(user.handle()));
	}

	@Test
	void registerInvalidData() throws Exception {
		RegisterRequest data = new RegisterRequest("username", "someemail.com", "secu", "handle");
		UserProjection user =
			UserProjection.builder().username(data.username()).handle(data.handle()).build();
		Mockito.when(userService.createUser(data)).thenReturn(user);
		mockMvc
			.perform(MockMvcRequestBuilders.post("/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(data)))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.message", Matchers.containsString("Validation failed")))
			.andExpect(jsonPath("$.errors.email").exists())
			.andExpect(jsonPath("$.errors.password").exists());
	}

	@Test
	void verifyAccountValid() throws Exception {
		String token = "valid token";
		Mockito.doNothing().when(userService).validateAccount(ArgumentMatchers.anyString());

		mockMvc.perform(MockMvcRequestBuilders.get("/auth/verify/account").param("token", token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", Matchers.containsString("Account verified")));
	}

	@Test
	void invalidTokenVerifyAccount() throws Exception {
		String token = "invalid token";
		Mockito.doThrow(new InvalidConfirmationTokenException())
			.when(userService)
			.validateAccount(ArgumentMatchers.anyString());

		mockMvc.perform(MockMvcRequestBuilders.get("/auth/verify/account").param("token", token))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message", Matchers.containsString("invalid")));
	}
}
