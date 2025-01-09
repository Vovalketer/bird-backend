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
import com.gray.bird.user.registration.AccountVerificationTokenEntity;
import com.gray.bird.user.registration.AccountVerificationTokenRepository;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final CredentialsRepository credentialsRepository;
	private final AccountVerificationTokenRepository verificationRepository;
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
		AccountVerificationTokenEntity confirmation = new AccountVerificationTokenEntity(
			user.getUuid(), LocalDateTime.now().plusSeconds(ACCOUNT_CONFIRMATION_EXPIRATION));
		verificationRepository.save(confirmation);
		publisher.publishEvent(
			new UserEvent(user, EventType.REGISTRATION, Map.of("token", confirmation.getToken())));
		return userMapper.toUserProjection(user);
	}

	public RoleEntity getRoleByName(String name) {
		RoleType type = RoleType.getType(name);
		return roleRepository.findByType(type).orElseThrow(() -> new ApiException("Role not found"));
	}

	public void validateAccount(String token) {
		AccountVerificationTokenEntity verificationToken = getUserConfirmation(token);
		if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new InvalidConfirmationTokenException();
		}
		UserEntity user = getUserEntityByUuid(verificationToken.getUserId());
		user.setEnabled(true);
		userRepository.save(user);
		verificationRepository.delete(verificationToken);
	}

	private AccountVerificationTokenEntity getUserConfirmation(String token) {
		return verificationRepository.findByToken(token).orElseThrow(
			() -> new ApiException("Token not valid"));
	}

	/*
	 * DTO functions
	 */

	public UserProjection getUserByUsername(String username) {
		UserEntity user = getUserEntityByUsername(username);
		return userMapper.toUserProjection(user);
	}

	public UserProjection getUserByUuid(UUID uuid) {
		UserEntity user =
			userRepository.findByUuid(uuid).orElseThrow(() -> new ApiException("No user found"));
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

	public UserEntity getUserEntityByUuid(UUID uuid) {
		return userRepository.findByUuid(uuid).orElseThrow(() -> new ApiException("No user found"));
	}

	private UserEntity createNewUser(String username, String handle, String email) {
		RoleEntity role = getRoleByName(RoleType.USER.name());
		var user = UserEntity.builder()
					   .uuid(UUID.randomUUID())
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

	public UUID getUserIdByUsername(String username) {
		return userRepository.findUuidByUsername(username).orElseThrow(
			() -> new ApiException("No user found"));
	}
}
