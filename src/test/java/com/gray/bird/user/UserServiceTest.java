package com.gray.bird.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import com.gray.bird.role.RoleEntity;
import com.gray.bird.role.RoleRepository;
import com.gray.bird.role.RoleType;
import com.gray.bird.user.dto.UserCreationRequest;
import com.gray.bird.user.dto.UserProjection;
import com.gray.bird.user.event.UserEventPublisher;
import com.gray.bird.utils.TestUtils;

@SpringJUnitConfig
public class UserServiceTest {
	@Mock
	private RoleRepository roleRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private CredentialsRepository credentialsRepository;
	@Mock
	private BCryptPasswordEncoder encoder;
	@Mock
	private UserEventPublisher publisher;
	@Mock
	private UserMapper userMapper;

	@InjectMocks
	private UserService userService;

	private TestUtils testUtils = new TestUtils();

	@Test
	void testCreateUser() {
		UserCreationRequest request =
			new UserCreationRequest("test_user", "test@test.com", "test_handle", "test_password");
		UserEntity user = testUtils.createUser(request.username(), request.handle(), request.email());
		RoleEntity role = testUtils.createRole(RoleType.USER);
		CredentialsEntity credentials = testUtils.createCredentials(user, "test_password");
		UserProjection userProjection = Mockito.mock(UserProjection.class);

		Mockito.when(roleRepository.findByType(RoleType.USER)).thenReturn(Optional.of(role));
		Mockito.when(userRepository.save(Mockito.any(UserEntity.class))).thenReturn(user);
		Mockito.when(credentialsRepository.save(Mockito.any(CredentialsEntity.class)))
			.thenReturn(credentials);
		Mockito.when(encoder.encode(request.password())).thenReturn("test_password");
		Mockito.doNothing().when(publisher).publishUserCreatedEvent(
			Mockito.any(UUID.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.when(userMapper.toUserProjection(Mockito.any(UserEntity.class))).thenReturn(userProjection);

		userService.createUser(request);

		Mockito.verify(roleRepository, Mockito.times(1)).findByType(RoleType.USER);
		Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(UserEntity.class));
		Mockito.verify(credentialsRepository, Mockito.times(1)).save(Mockito.any(CredentialsEntity.class));
		Mockito.verify(encoder, Mockito.times(1)).encode(request.password());
		Mockito.verify(publisher, Mockito.times(1))
			.publishUserCreatedEvent(user.getUuid(), user.getUsername(), user.getHandle(), user.getEmail());
	}

	@Test
	void testEnableAccount() {
		UserEntity user = testUtils.createUser();
		user.setEnabled(false);
		Mockito.when(userRepository.findByUuid(user.getUuid())).thenReturn(Optional.of(user));
		Mockito.when(userRepository.save(Mockito.any(UserEntity.class))).thenReturn(user);

		userService.enableAccount(user.getUuid());

		Assertions.assertThat(user.isEnabled()).isTrue();
		Mockito.verify(userRepository, Mockito.times(1)).findByUuid(user.getUuid());
		Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(UserEntity.class));
	}
}
