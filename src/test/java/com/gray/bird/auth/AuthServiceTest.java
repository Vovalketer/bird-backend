package com.gray.bird.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.servlet.http.Cookie;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.CharBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.gray.bird.auth.dto.LoginRequest;
import com.gray.bird.auth.dto.LoginResponse;
import com.gray.bird.auth.event.AuthEventPublisher;
import com.gray.bird.auth.jwt.JwtService;
import com.gray.bird.auth.jwt.TokenData;
import com.gray.bird.auth.jwt.TokenType;
import com.gray.bird.common.HttpUtils;
import com.gray.bird.exception.GlobalExceptionHandler;
import com.gray.bird.exception.InvalidJwtException;
import com.gray.bird.exception.UnauthorizedException;
import com.gray.bird.role.RoleType;
import com.gray.bird.user.dto.CredentialsDto;
import com.gray.bird.user.dto.UserDataDto;
import com.gray.bird.utils.TestUtils;
import com.gray.bird.utils.TestUtilsFactory;

@Import(GlobalExceptionHandler.class)
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
	@Mock
	private JwtService jwtService;
	@Mock
	private AuthCache userCache;
	@Mock
	private BCryptPasswordEncoder encoder;
	@Mock
	private UserPrincipalService userPrincipalService;
	@Mock
	private AuthEventPublisher publisher;
	@InjectMocks
	private AuthService authService;
	private TestUtils testUtils = TestUtilsFactory.createTestUtils();

	@Test
	void testValidLogin() {
		// given
		String email = "valid@email";
		String username = "testusername";
		String rawPassword = "securepassword";
		String handle = "testhandle";
		LoginRequest loginRequest = new LoginRequest(email, rawPassword);
		UserDataDto testUser = testUtils.createUserDto(username, handle, email);
		CredentialsDto credentials = new CredentialsDto(rawPassword.toCharArray());
		UserPrincipal principal = new UserPrincipal(testUser, credentials);
		String accessTok = "mockAccessToken";
		Cookie refreshTok = new Cookie(TokenType.REFRESH.getValue(), "mockRefreshToken");

		// when
		when(userPrincipalService.loadUserByEmail(loginRequest.email())).thenReturn(principal);
		when(encoder.matches(Mockito.any(CharBuffer.class), Mockito.anyString())).thenReturn(true);
		when(jwtService.createAccessToken(any(UserPrincipal.class))).thenReturn(accessTok);
		when(jwtService.createRefreshToken(any(UserPrincipal.class))).thenReturn(refreshTok);
		doNothing().when(publisher).publishUserLoggedInEvent(any(UUID.class));

		LoginResponse loginResponse = authService.login(loginRequest);

		// then
		Assertions.assertThat(loginResponse).isNotNull();
		Assertions.assertThat(loginResponse.accessToken()).isEqualTo(accessTok);
		Assertions.assertThat(loginResponse.refreshToken()).isEqualTo(refreshTok);
	}

	@Test
	void invalidPasswordLogin() {
		// given
		String email = "valid@email";
		String rawPassword = "securepassword";
		LoginRequest loginRequest = new LoginRequest(email, rawPassword);
		UserPrincipal principal =
			new UserPrincipal(testUtils.createUserDto(), testUtils.createCredentialsDto());

		// when
		when(userPrincipalService.loadUserByEmail(anyString())).thenReturn(principal);
		when(encoder.matches(Mockito.any(CharBuffer.class), Mockito.anyString())).thenReturn(false);

		// then
		Assertions.assertThatThrownBy(() -> authService.login(loginRequest))
			.isInstanceOf(BadCredentialsException.class);
	}

	@Test
	void lockUserWhenTooManyLoginAttempts() {
		// given
		String email = "valid@email";
		String rawPassword = "incorrect_password";
		LoginRequest loginRequest = new LoginRequest(email, rawPassword);
		UserPrincipal principal =
			new UserPrincipal(testUtils.createUserDto(), testUtils.createCredentialsDto());

		// when
		when(userPrincipalService.loadUserByEmail(anyString())).thenReturn(principal);
		when(userCache.getLoginAttempts(anyString())).thenReturn(AuthConstants.MAX_LOGIN_ATTEMPTS + 1);

		// then
		Assertions.assertThatThrownBy(() -> authService.login(loginRequest))
			.isInstanceOf(DisabledException.class);
	}

	@Test
	void returnValidAccessTokenCookieWhenRefreshTokenIsProvided() {
		// given
		String access = "accessmockvalue";
		Cookie refresh = HttpUtils.createCookie(TokenType.REFRESH.getValue(), "refreshmockvalue");
		Cookie[] cookies = {refresh};
		Set<String> audience = new HashSet<String>();
		audience.add("audience");
		UserPrincipal userPrincipal = testUtils.createUserPrincipal();
		TokenData tokenData = new TokenData(userPrincipal.getUsername(), audience, RoleType.USER);

		// when
		when(jwtService.validateRefreshToken(anyString())).thenReturn(true);
		when(jwtService.getDataFromToken(anyString())).thenReturn(tokenData);
		when(userPrincipalService.loadUserByUsername(anyString())).thenReturn(userPrincipal);
		when(jwtService.createAccessToken(userPrincipal)).thenReturn(access);

		String accessTokenCookie = authService.refreshAccessToken(cookies);

		// then
		Assertions.assertThat(accessTokenCookie).isNotNull();
		Assertions.assertThat(accessTokenCookie).isEqualTo(access);
	}

	@Test
	void refreshThrowsWhenNoCookies() {
		Cookie[] cookies = null;

		Assertions.assertThatThrownBy(() -> authService.refreshAccessToken(cookies))
			.isInstanceOf(UnauthorizedException.class);
	}

	@Test
	void invalidRefreshTokenShouldThrowException() {
		Cookie refresh = HttpUtils.createCookie(TokenType.REFRESH.getValue(), "invalidtoken");
		Mockito.when(jwtService.validateRefreshToken(Mockito.anyString())).thenReturn(false);

		Assertions.assertThatThrownBy(() -> authService.refreshAccessToken(new Cookie[] {refresh}))
			.isInstanceOf(InvalidJwtException.class);
	}

	@Test
	void disabledAccountShouldThrowException() {
		UserDataDto data = UserDataDto.builder()
							   .enabled(false)
							   .accountNonLocked(true)
							   .credentialsNonExpired(true)
							   .accountNonExpired(true)
							   .build();
		CredentialsDto creds = new CredentialsDto("password".toCharArray());
		UserPrincipal user = new UserPrincipal(data, creds);
		LoginRequest userRequest = new LoginRequest("user1@email.com", "password");

		Mockito.when(userPrincipalService.loadUserByEmail(Mockito.anyString())).thenReturn(user);

		Assertions.assertThatThrownBy(() -> authService.login(userRequest))
			.isInstanceOf(DisabledException.class);
	}

	@Test
	void lockedAccountShouldThrowException() {
		UserDataDto data = UserDataDto.builder()
							   .enabled(true)
							   .accountNonLocked(false)
							   .credentialsNonExpired(true)
							   .accountNonExpired(true)
							   .build();
		CredentialsDto creds = new CredentialsDto("password".toCharArray());
		UserPrincipal user = new UserPrincipal(data, creds);
		LoginRequest userRequest = new LoginRequest("user1@email.com", "password");

		Mockito.when(userPrincipalService.loadUserByEmail(Mockito.anyString())).thenReturn(user);

		Assertions.assertThatThrownBy(() -> authService.login(userRequest))
			.isInstanceOf(LockedException.class);
	}

	@Test
	void expiredCredentialsShouldThrowException() {
		UserDataDto data = UserDataDto.builder()
							   .enabled(true)
							   .accountNonLocked(true)
							   .credentialsNonExpired(false)
							   .accountNonExpired(true)
							   .build();
		CredentialsDto creds = new CredentialsDto("password".toCharArray());
		UserPrincipal user = new UserPrincipal(data, creds);
		LoginRequest userRequest = new LoginRequest("user1@email.com", "password");

		Mockito.when(userPrincipalService.loadUserByEmail(Mockito.anyString())).thenReturn(user);

		Assertions.assertThatThrownBy(() -> authService.login(userRequest))
			.isInstanceOf(CredentialsExpiredException.class);
	}

	@Test
	void expiredAccountShouldThrowException() {
		UserDataDto data = UserDataDto.builder()
							   .enabled(true)
							   .accountNonLocked(true)
							   .credentialsNonExpired(true)
							   .accountNonExpired(false)
							   .build();
		CredentialsDto creds = new CredentialsDto("password".toCharArray());
		UserPrincipal user = new UserPrincipal(data, creds);
		LoginRequest userRequest = new LoginRequest("user1@email.com", "password");

		Mockito.when(userPrincipalService.loadUserByEmail(Mockito.anyString())).thenReturn(user);

		Assertions.assertThatThrownBy(() -> authService.login(userRequest))
			.isInstanceOf(DisabledException.class);
	}
}
