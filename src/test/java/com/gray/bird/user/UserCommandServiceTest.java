package com.gray.bird.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

import com.gray.bird.role.RoleEntity;
import com.gray.bird.role.RoleRepository;
import com.gray.bird.role.RoleType;
import com.gray.bird.user.command.EnableAccountCommand;
import com.gray.bird.user.command.RegisterUserCommand;
import com.gray.bird.user.event.UserEventPublisher;
import com.gray.bird.utils.TestUtils;

@SpringJUnitConfig
public class UserCommandServiceTest {
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

	@InjectMocks
	private UserCommandService userCommandService;

	private TestUtils testUtils = new TestUtils();

	@Test
	void testCreateUser() {
		RegisterUserCommand command =
			new RegisterUserCommand("test_user", "test@test.com", "test_handle", "test_password");
		UserEntity user = testUtils.createUser(command.username(), command.handle(), command.email());
		RoleEntity role = testUtils.createRole(RoleType.USER);
		CredentialsEntity credentials = testUtils.createCredentials(user, "test_password");

		Mockito.when(roleRepository.findByType(RoleType.USER)).thenReturn(Optional.of(role));
		Mockito.when(userRepository.save(Mockito.any(UserEntity.class))).thenReturn(user);
		Mockito.when(credentialsRepository.save(Mockito.any(CredentialsEntity.class)))
			.thenReturn(credentials);
		Mockito.when(encoder.encode(command.password())).thenReturn("test_password");
		Mockito.doNothing().when(publisher).publishUserCreatedEvent(
			Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

		userCommandService.createUser(command);

		Mockito.verify(roleRepository, Mockito.times(1)).findByType(RoleType.USER);
		Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(UserEntity.class));
		Mockito.verify(credentialsRepository, Mockito.times(1)).save(Mockito.any(CredentialsEntity.class));
		Mockito.verify(encoder, Mockito.times(1)).encode(command.password());
		Mockito.verify(publisher, Mockito.times(1))
			.publishUserCreatedEvent(user.getId(), user.getReferenceId(), user.getHandle(), user.getEmail());
	}

	@Test
	void testEnableAccount() {
		UserEntity user = testUtils.createUser();
		user.setEnabled(false);
		Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		Mockito.when(userRepository.save(Mockito.any(UserEntity.class))).thenReturn(user);

		userCommandService.enableAccount(new EnableAccountCommand(user.getId()));

		Assertions.assertThat(user.isEnabled()).isTrue();
		Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
		Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(UserEntity.class));
	}
}
