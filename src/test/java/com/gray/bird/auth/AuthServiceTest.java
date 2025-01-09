package com.gray.bird.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.gray.bird.exception.UnauthorizedException;
import com.gray.bird.role.RoleType;
import com.gray.bird.user.UserEntity;
import com.gray.bird.user.UserMapper;
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

	@Autowired
	private UserMapper userMapper;

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
		Cookie accessTok = new Cookie(TokenType.ACCESS.getValue(), "mockAccessToken");
		Cookie refreshTok = new Cookie(TokenType.REFRESH.getValue(), "mockRefreshToken");

		// when
		when(userPrincipalService.loadUserByEmail(loginRequest.email())).thenReturn(principal);
		when(encoder.matches(Mockito.any(CharBuffer.class), Mockito.anyString())).thenReturn(true);
		when(jwtService.createJwtCookie(any(UserPrincipal.class), eq(TokenType.ACCESS)))
			.thenReturn(accessTok);
		when(jwtService.createJwtCookie(any(UserPrincipal.class), eq(TokenType.REFRESH)))
			.thenReturn(refreshTok);
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
		Cookie access = HttpUtils.createCookie(TokenType.ACCESS.getValue(), "accessmockvalue");
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
		when(jwtService.createJwtCookie(userPrincipal, TokenType.ACCESS)).thenReturn(access);

		Cookie accessTokenCookie = authService.refreshAccessToken(cookies);

		// then
		Assertions.assertThat(accessTokenCookie).isNotNull();
		Assertions.assertThat(accessTokenCookie.getName()).isEqualTo(TokenType.ACCESS.getValue());
	}

	@Test
	void refreshThrowsWhenNoCookies() {
		Cookie[] cookies = null;

		Assertions.assertThatThrownBy(() -> authService.refreshAccessToken(cookies))
			.isInstanceOf(UnauthorizedException.class);
	}

	@Test
	void testValidateAccount() {
		LoginRequest user1Request = new LoginRequest("user1@email.com", "password");
		LoginRequest user2Request = new LoginRequest("user2@email.com", "password");
		LoginRequest user3Request = new LoginRequest("user3@email.com", "password");
		LoginRequest user4Request = new LoginRequest("user4@email.com", "password");

		UserEntity user1 = testUtils.createUser("user", "handle", user1Request.email());
		user1.setEnabled(false);
		UserEntity user2 = testUtils.createUser("user", "handle", user2Request.email());
		user2.setAccountNonLocked(false);
		UserEntity user3 = testUtils.createUser("user", "handle", user3Request.email());
		user3.setCredentialsNonExpired(false);
		UserEntity user4 = testUtils.createUser("user", "handle", user4Request.email());
		user4.setAccountNonExpired(false);
		UserPrincipal principal1 =
			new UserPrincipal(userMapper.toUserDto(user1), testUtils.createCredentialsDto());
		UserPrincipal principal2 =
			new UserPrincipal(userMapper.toUserDto(user2), testUtils.createCredentialsDto());
		UserPrincipal principal3 =
			new UserPrincipal(userMapper.toUserDto(user3), testUtils.createCredentialsDto());
		UserPrincipal principal4 =
			new UserPrincipal(userMapper.toUserDto(user4), testUtils.createCredentialsDto());

		when(userPrincipalService.loadUserByEmail(user1Request.email())).thenReturn(principal1);
		when(userPrincipalService.loadUserByEmail(user2Request.email())).thenReturn(principal2);
		when(userPrincipalService.loadUserByEmail(user3Request.email())).thenReturn(principal3);
		when(userPrincipalService.loadUserByEmail(user4Request.email())).thenReturn(principal4);

		Assertions.assertThatThrownBy(() -> authService.login(user1Request))
			.isInstanceOf(DisabledException.class);
		Assertions.assertThatThrownBy(() -> authService.login(user2Request))
			.isInstanceOf(LockedException.class);
		Assertions.assertThatThrownBy(() -> authService.login(user3Request))
			.isInstanceOf(CredentialsExpiredException.class);
		Assertions.assertThatThrownBy(() -> authService.login(user4Request))
			.isInstanceOf(DisabledException.class);
	}
}
