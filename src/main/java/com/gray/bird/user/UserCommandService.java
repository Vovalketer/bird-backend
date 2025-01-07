package com.gray.bird.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import com.gray.bird.exception.ResourceNotFoundException;
import com.gray.bird.exception.RoleNotFoundException;
import com.gray.bird.role.RoleEntity;
import com.gray.bird.role.RoleRepository;
import com.gray.bird.role.RoleType;
import com.gray.bird.user.command.EnableAccountCommand;
import com.gray.bird.user.command.RegisterUserCommand;
import com.gray.bird.user.event.UserEventPublisher;

@Service
@RequiredArgsConstructor
public class UserCommandService {
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final CredentialsRepository credentialsRepository;
	private final BCryptPasswordEncoder encoder;
	private final UserEventPublisher publisher;

	public void createUser(RegisterUserCommand command) {
		UserEntity user = createUserEntity(command);
		UserEntity savedUser = userRepository.save(user);

		CredentialsEntity credentials = new CredentialsEntity(savedUser, encoder.encode(command.password()));
		credentialsRepository.save(credentials);

		publisher.publishUserCreatedEvent(
			savedUser.getId(), savedUser.getReferenceId(), savedUser.getHandle(), savedUser.getEmail());
	}

	public void enableAccount(EnableAccountCommand command) {
		UserEntity user = getUserEntity(command.userId());
		user.setEnabled(true);
		userRepository.save(user);
	}

	private UserEntity createUserEntity(RegisterUserCommand command) {
		RoleEntity role =
			roleRepository.findByType(RoleType.USER).orElseThrow(() -> new RoleNotFoundException());
		return UserEntity.builder()
			.referenceId(UUID.randomUUID().toString())
			.username(command.username())
			.email(command.email())
			.handle(command.handle())
			// security
			.lastLogin(LocalDateTime.now())
			.accountNonExpired(true)
			.accountNonLocked(true)
			.credentialsNonExpired(true)
			.enabled(false)
			.role(role)
			// details
			.dateOfBirth(null)
			.bio(null)
			.location(null)
			.profileImage(null) // TODO: set the default pic here
			.build();
	}

	private UserEntity getUserEntity(Long userId) {
		UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException());
		return user;
	}
}
