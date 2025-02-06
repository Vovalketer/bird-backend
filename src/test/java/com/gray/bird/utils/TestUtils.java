package com.gray.bird.utils;

import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.gray.bird.auth.UserPrincipal;
import com.gray.bird.media.MediaMapper;
import com.gray.bird.media.MediaMapperImpl;
import com.gray.bird.post.PostEntity;
import com.gray.bird.post.PostMapper;
import com.gray.bird.post.PostMapperImpl;
import com.gray.bird.post.ReplyType;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.postAggregator.PostAggregate;
import com.gray.bird.postAggregator.dto.PostInteractions;
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
							  .dateOfBirth(LocalDate.now().minus(randomInt(7200), ChronoUnit.DAYS))
							  .bio(UUID.randomUUID().toString())
							  .location(UUID.randomUUID().toString())
							  .profileImage("http://www.example.com/" + UUID.randomUUID().toString() + ".jpg")
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

	public PostEntity createPost(UserEntity user) {
		return createPost(user, ReplyType.EVERYONE, false, null);
	}

	public List<PostEntity> createPosts(UserEntity user, int count) {
		List<PostEntity> posts = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			posts.add(createPost(user));
		}
		return posts;
	}

	public PostEntity createPost() {
		return createPost(createUser(), ReplyType.EVERYONE, false, null);
	}

	public PostEntity createPost(UUID userId) {
		PostEntity post = createPost(createUser(), ReplyType.EVERYONE, false, null);
		post.setUserId(userId);
		return post;
	}

	public PostEntity createReply() {
		PostEntity parent = createPost(createUser(), ReplyType.EVERYONE, false, null);

		PostEntity reply = createPost(createUser(), ReplyType.EVERYONE, false, parent);
		reply.setParentPostId(parent.getId());

		return reply;
	}

	public PostProjection createPostProjection() {
		PostEntity post = createPost();
		return postMapper.toPostProjection(post);
	}

	public PostProjection createPostProjection(UUID userId) {
		PostEntity post = createPost(userId);
		return postMapper.toPostProjection(post);
	}

	public PostProjection createPostProjection(UUID userId, Long id) {
		PostEntity post = createPost(userId);
		post.setId(id);
		return postMapper.toPostProjection(post);
	}

	public PostProjection createReplyPostProjection(Long parentPostId) {
		PostEntity post = createPost();
		post.setParentPostId(parentPostId);
		return postMapper.toPostProjection(post);
	}

	public PostInteractions createPostInteractions(Long postId) {
		PostInteractions postInteractions =
			new PostInteractions(postId, randomInt(), randomInt(), randomInt());
		return postInteractions;
	}

	public UserProjection createUserProjection() {
		return userMapper.toUserProjection(createUser());
	}

	public UserProjection createUserProjection(UUID userId) {
		UserEntity user = createUser();
		user.setUuid(userId);
		return userMapper.toUserProjection(user);
	}

	public PostAggregate createPostAggregateWithoutMedia() {
		PostProjection postProjection = createPostProjection();
		PostInteractions postInteractions = createPostInteractions(postProjection.id());
		return new PostAggregate(postProjection, new ArrayList<>(), Optional.of(postInteractions));
	}

	public PostAggregate createPostAggregateWithoutMedia(UUID userId, Long id) {
		PostProjection postProjection = createPostProjection(userId, id);
		PostInteractions postInteractions = createPostInteractions(id);
		return new PostAggregate(postProjection, new ArrayList<>(), Optional.of(postInteractions));
	}

	public PostAggregate createReplyPostAggregateWithoutMedia(Long parentPostId) {
		PostProjection replyPostProjection = createReplyPostProjection(parentPostId);
		PostInteractions postInteractions = createPostInteractions(replyPostProjection.id());
		return new PostAggregate(replyPostProjection, new ArrayList<>(), Optional.of(postInteractions));
	}

	public List<PostAggregate> createListOfPostAggregateWithoutMedia(int count) {
		List<PostAggregate> postAggregates = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			postAggregates.add(createPostAggregateWithoutMedia());
		}
		return postAggregates;
	}

	public List<PostAggregate> createReplyPostAggregateWithoutMedia(Long parentPostId, int count) {
		List<PostAggregate> postAggregates = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			postAggregates.add(createReplyPostAggregateWithoutMedia(parentPostId));
		}
		return postAggregates;
	}

	private long randomId() {
		return Math.abs(ThreadLocalRandom.current().nextLong());
	}

	private long randomInt() {
		return Math.abs(ThreadLocalRandom.current().nextInt());
	}

	private long randomInt(int max) {
		return ThreadLocalRandom.current().nextInt(max);
	}
}
