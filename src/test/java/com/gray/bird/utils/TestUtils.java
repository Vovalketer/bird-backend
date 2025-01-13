package com.gray.bird.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.gray.bird.auth.UserPrincipal;
import com.gray.bird.media.MediaMapper;
import com.gray.bird.media.MediaMapperImpl;
import com.gray.bird.post.PostEntity;
import com.gray.bird.post.PostMapper;
import com.gray.bird.post.PostMapperImpl;
import com.gray.bird.post.ReplyType;
import com.gray.bird.postAggregator.PostAggregate;
import com.gray.bird.postAggregator.PostAggregateMapper;
import com.gray.bird.postAggregator.PostAggregateMapperImpl;
import com.gray.bird.role.RoleEntity;
import com.gray.bird.role.RoleType;
import com.gray.bird.security.SecurityConstants;
import com.gray.bird.user.CredentialsEntity;
import com.gray.bird.user.UserEntity;
import com.gray.bird.user.UserMapper;
import com.gray.bird.user.UserMapperImpl;
import com.gray.bird.user.dto.CredentialsDto;
import com.gray.bird.user.dto.UserDataDto;
import com.gray.bird.user.dto.UserProjection;

@Component
public class TestUtils {
	private final UserMapper userMapper = new UserMapperImpl();
	private final MediaMapper mediaMapper = new MediaMapperImpl();
	private final PostMapper postMapper = new PostMapperImpl();
	private final PostAggregateMapper postAggregateMapper = new PostAggregateMapperImpl(postMapper);

	public UserEntity createUser(String username, String handle, String email) {
		RoleEntity role = createRole(RoleType.USER);
		UserEntity user = UserEntity.builder()
							  .id(randomId())
							  .uuid(UUID.randomUUID())
							  .username(username)
							  .email(email)
							  .handle(handle)
							  .lastLogin(LocalDateTime.now())
							  .accountNonExpired(true)
							  .accountNonLocked(true)
							  .credentialsNonExpired(true)
							  .enabled(true)
							  .role(role)
							  .dateOfBirth(null)
							  .bio(null)
							  .location(null)
							  .profileImage(null)
							  .build();
		return user;
	}

	public UserEntity createUserWithRole(
		Long id, String username, String handle, String email, RoleType role) {
		RoleEntity roleEntity = createRole(role);
		UserEntity user = UserEntity.builder()
							  .id(randomId())
							  .username(username)
							  .uuid(UUID.randomUUID())
							  .email(email)
							  .handle(handle)
							  // security
							  .lastLogin(LocalDateTime.now())
							  .accountNonExpired(true)
							  .accountNonLocked(true)
							  .credentialsNonExpired(true)
							  .enabled(false)
							  .role(roleEntity)
							  // fluff
							  .dateOfBirth(null)
							  .bio(null)
							  .location(null)
							  .profileImage(null)
							  .build();
		return user;
	}

	public UserEntity createUser() {
		return createUser("test_user", "test_handle", "some@email.com");
	}

	public RoleEntity createRole(RoleType role) {
		return new RoleEntity(Long.valueOf(role.ordinal()), role);
	}

	public CredentialsEntity createCredentialsWithEncryptedPassword(UserEntity user, String password) {
		BCryptPasswordEncoder encoder =
			new BCryptPasswordEncoder(SecurityConstants.PASSWORD_ENCODER_STRENGTH);
		return new CredentialsEntity(user, encoder.encode(password));
	}

	public CredentialsEntity createCredentials(UserEntity user, String password) {
		return new CredentialsEntity(user, password);
	}

	public CredentialsDto createCredentialsDto(String password) {
		return new CredentialsDto(password.toCharArray());
	}

	public CredentialsDto createCredentialsDto() {
		return new CredentialsDto("securepassword".toCharArray());
	}

	public UserDataDto createUserDto(String username, String handle, String email) {
		return userMapper.toUserDto(createUser(username, handle, email));
	}

	public UserDataDto createUserDto() {
		return userMapper.toUserDto(createUser());
	}

	public UserEntity setAccountAsValid(UserEntity user) {
		user.setAccountNonExpired(true);
		user.setCredentialsNonExpired(true);
		user.setEnabled(true);
		user.setAccountNonLocked(true);
		return user;
	}

	public UserPrincipal createUserPrincipal() {
		return new UserPrincipal(createUserDto(), createCredentialsDto());
	}

	public PostEntity createPost(
		UserEntity user, ReplyType replyType, boolean deleted, PostEntity parentPost) {
		return PostEntity.builder()
			.id(randomId())
			.userId(user.getUuid())
			.text(UUID.randomUUID().toString())
			.replyType(replyType)
			.deleted(deleted)
			.parentPost(parentPost)
			.build();
	}

	public PostEntity createPost() {
		return createPost(createUser(), ReplyType.EVERYONE, false, null);
	}

	public PostEntity createReply() {
		PostEntity parent = createPost(createUser(), ReplyType.EVERYONE, false, null);

		PostEntity reply = createPost(createUser(), ReplyType.EVERYONE, false, parent);
		reply.setParentPostId(parent.getId());

		return reply;
	}

	public UserProjection createUserProjection() {
		return userMapper.toUserProjection(createUser());
	}

	public PostAggregate createPostAggregate() {
		PostEntity post = createPost();
		return postAggregateMapper.toPostAggregate(post);
	}

	private long randomId() {
		return ThreadLocalRandom.current().nextLong();
	}

	private long randomInt() {
		return ThreadLocalRandom.current().nextInt();
	}
}
