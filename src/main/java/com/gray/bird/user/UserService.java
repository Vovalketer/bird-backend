package com.gray.bird.user;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.gray.bird.exception.ApiException;
import com.gray.bird.exception.InvalidConfirmationTokenException;
import com.gray.bird.exception.ResourceNotFoundException;
import com.gray.bird.role.RoleEntity;
import com.gray.bird.role.RoleRepository;
import com.gray.bird.role.RoleType;
import com.gray.bird.user.dto.RegisterRequest;
import com.gray.bird.user.dto.UserProjection;
import com.gray.bird.user.event.EventType;
import com.gray.bird.user.event.UserEvent;
import com.gray.bird.user.registration.Confirmation;
import com.gray.bird.user.registration.ConfirmationRepository;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final CredentialsRepository credentialsRepository;
	private final ConfirmationRepository confirmationRepository;
	private final BCryptPasswordEncoder encoder;
	private final ApplicationEventPublisher publisher;
	private final UserMapper userMapper;

	// TODO: move this into the application yml
	private final Integer ACCOUNT_CONFIRMATION_EXPIRATION = 86400;

	/*
	 * User Registration
	 */

	public UserProjection createUser(RegisterRequest data) {
		UserEntity user = userRepository.save(createNewUser(data.username(), data.handle(), data.email()));

		CredentialsEntity credential = new CredentialsEntity(user, encoder.encode(data.password()));
		credentialsRepository.save(credential);
		Confirmation confirmation =
			new Confirmation(user, LocalDateTime.now().plusSeconds(ACCOUNT_CONFIRMATION_EXPIRATION));
		confirmationRepository.save(confirmation);
		publisher.publishEvent(
			new UserEvent(user, EventType.REGISTRATION, Map.of("token", confirmation.getToken())));
		return userMapper.toUserProjection(user);
	}

	public RoleEntity getRoleByName(String name) {
		RoleType type = RoleType.getType(name);
		return roleRepository.findByType(type).orElseThrow(() -> new ApiException("Role not found"));
	}

	public void validateAccount(String token) {
		Confirmation confirmation = getUserConfirmation(token);
		if (confirmation.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new InvalidConfirmationTokenException();
		}
		UserEntity user = getUserEntityByEmail(confirmation.getUser().getEmail());
		user.setEnabled(true);
		userRepository.save(user);
		confirmationRepository.delete(confirmation);
	}

	private Confirmation getUserConfirmation(String token) {
		return confirmationRepository.findByToken(token).orElseThrow(
			() -> new ApiException("Token not valid"));
	}

	/*
	 * DTO functions
	 */

	public UserProjection getUserByUsername(String username) {
		UserEntity user = getUserEntityByUsername(username);
		return userMapper.toUserProjection(user);
	}

	public UserProjection getUserByReferenceId(String referenceId) {
		UserEntity user = userRepository.findByReferenceId(referenceId)
							  .orElseThrow(() -> new ApiException("No user found"));
		return userMapper.toUserProjection(user);
	}

	public UserProjection getUserById(Long userId) {
		UserEntity user = getUserEntityById(userId);
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

	/*
	 * Entity Functions
	 */

	public UserEntity getUserEntityByUsername(String username) {
		return userRepository.findByUsernameIgnoreCase(username).orElseThrow(
			() -> new ResourceNotFoundException());
	}

	public UserEntity getUserEntityByEmail(String email) {
		return userRepository.findByEmailIgnoreCase(email).orElseThrow(
			() -> new ApiException("No user found with this email"));
	}

	public UserEntity getUserEntityById(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new ApiException("No user found"));
	}

	public UserEntity getUserEntityByReferenceId(String referenceId) {
		return userRepository.findByReferenceId(referenceId)
			.orElseThrow(() -> new ApiException("No user found"));
	}

	private UserEntity createNewUser(String username, String handle, String email) {
		RoleEntity role = getRoleByName(RoleType.USER.name());
		var user = UserEntity.builder()
					   .referenceId(UUID.randomUUID().toString())
					   .username(username)
					   .email(email)
					   .handle(handle)
					   // security
					   .lastLogin(LocalDateTime.now())
					   .accountNonExpired(true)
					   .accountNonLocked(true)
					   .credentialsNonExpired(true)
					   .enabled(false)
					   .role(role)
					   // fluff
					   .dateOfBirth(null)
					   .bio(null)
					   .location(null)
					   .profileImage(null) // TODO: set the default pic here
					   .build();
		return user;
	}

	public UserEntity save(UserEntity user) {
		return userRepository.save(user);
	}

	public void updateLastLogin(String username, LocalDateTime loginDateTime) {
		UserEntity user = getUserEntityByUsername(username);
		user.setLastLogin(loginDateTime);
		save(user);
	}
}
