package com.gray.bird.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.gray.bird.exception.ApiException;
import com.gray.bird.exception.ResourceNotFoundException;
import com.gray.bird.exception.RoleNotFoundException;
import com.gray.bird.role.RoleEntity;
import com.gray.bird.role.RoleRepository;
import com.gray.bird.role.RoleType;
import com.gray.bird.user.dto.UserCreationRequest;
import com.gray.bird.user.dto.UserProjection;
import com.gray.bird.user.event.UserEventPublisher;
import com.gray.bird.user.registration.AccountVerificationService;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final CredentialsRepository credentialsRepository;
	private final BCryptPasswordEncoder encoder;
	private final UserEventPublisher publisher;
	private final UserMapper userMapper;
	private final AccountVerificationService accountVerificationService;

	/*
	 * User tasks
	 */

	public UserProjection createUser(UserCreationRequest request) {
		UserEntity user = createUserEntity(request);
		UserEntity savedUser = userRepository.save(user);

		CredentialsEntity credentials = new CredentialsEntity(savedUser, encoder.encode(request.password()));
		credentialsRepository.save(credentials);
		String verificationToken = accountVerificationService.createVerificationToken(savedUser.getUuid());

		publisher.publishUserCreatedEvent(savedUser.getHandle(), savedUser.getEmail(), verificationToken);

		return userMapper.toUserProjection(savedUser);
	}

	public RoleEntity getRoleByName(String name) {
		RoleType type = RoleType.getType(name);
		return roleRepository.findByType(type).orElseThrow(() -> new ApiException("Role not found"));
	}

	public void enableAccount(UUID userId) {
		UserEntity user = getUserEntityByUuid(userId);
		user.setEnabled(true);
		userRepository.save(user);
	}

	public void updateLastLogin(UUID userId, LocalDateTime time) {
		// the time is better off sent as a parameter since async tasks might throw it off
		UserEntity user = getUserEntityByUuid(userId);
		user.setLastLogin(time);
		save(user);
	}

	/*
	 * Query functions
	 */

	public UserProjection getUserByUsername(String username) {
		UserEntity user = getUserEntityByUsername(username);
		return userMapper.toUserProjection(user);
	}

	public UserProjection getUserById(UUID uuid) {
		UserEntity user = userRepository.findByUuid(uuid).orElseThrow(() -> new ResourceNotFoundException());
		return userMapper.toUserProjection(user);
	}

	public UserProjection getUserByEmail(String email) {
		UserEntity user = getUserEntityByEmail(email);
		return userMapper.toUserProjection(user);
	}

	public UserProjection getUserProfile(String username) {
		UserEntity user = getUserEntityByUsername(username);
		return userMapper.toUserProjection(user);
	}

	public List<UserProjection> getAllUsersById(Iterable<UUID> userIds) {
		return userRepository.findAllByUuidIn(userIds, UserProjection.class);
	}

	public UUID getUserIdByUsername(String username) {
		return userRepository.findUuidByUsername(username).orElseThrow(() -> new ResourceNotFoundException());
	}

	/*
	 * Entity Functions
	 */

	public UserEntity getUserEntityByUsername(String username) {
		return userRepository.findByUsernameIgnoreCase(username).orElseThrow(
			() -> new ResourceNotFoundException());
	}

	public UserEntity getUserEntityByEmail(String email) {
		return userRepository.findByEmailIgnoreCase(email).orElseThrow(
			() -> new ResourceNotFoundException("No user found with this email"));
	}

	public UserEntity getUserEntityById(Long userId) {
		return userRepository.findById(userId).orElseThrow(
			() -> new ResourceNotFoundException("User not found"));
	}

	public UserEntity getUserEntityByUuid(UUID uuid) {
		return userRepository.findByUuid(uuid).orElseThrow(() -> new ResourceNotFoundException());
	}

	private UserEntity createUserEntity(UserCreationRequest request) {
		// TODO: move the role lookup into a service
		RoleEntity role =
			roleRepository.findByType(RoleType.USER).orElseThrow(() -> new RoleNotFoundException());
		return UserEntity.builder()
			.uuid(UUID.randomUUID())
			.username(request.username())
			.email(request.email())
			.handle(request.handle())
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

	public UserEntity save(UserEntity user) {
		return userRepository.save(user);
	}
}
