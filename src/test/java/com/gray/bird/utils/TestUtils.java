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
import com.gray.bird.media.view.MediaView;
import com.gray.bird.post.PostEntity;
import com.gray.bird.post.PostMapper;
import com.gray.bird.post.PostMapperImpl;
import com.gray.bird.post.ReplyType;
import com.gray.bird.post.view.InteractionsView;
import com.gray.bird.post.view.PostView;
import com.gray.bird.postAggregate.PostAggregate;
import com.gray.bird.postAggregate.PostAggregateMapper;
import com.gray.bird.postAggregate.PostAggregateMapperImpl;
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
import com.gray.bird.user.view.UserView;

@Component
public class TestUtils {
	private final UserMapper userMapper = new UserMapperImpl();
	private final MediaMapper mediaMapper = new MediaMapperImpl();
	private final PostMapper postMapper = new PostMapperImpl(mediaMapper);
	private final PostAggregateMapper postAggregateMapper =
		new PostAggregateMapperImpl(postMapper, mediaMapper);

	public UserEntity createUser(String username, String handle, String email) {
		RoleEntity role = createRole(RoleType.USER);
		UserEntity user = UserEntity.builder()
							  .id(randomId())
							  .referenceId(UUID.randomUUID().toString())
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
							  .referenceId(UUID.randomUUID().toString())
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
			.user(user)
			.text(UUID.randomUUID().toString())
			.replyType(replyType)
			.deleted(deleted)
			.parentPost(parentPost)
			.build();
	}

	public PostEntity createPost() {
		var parent = createPost(createUser(), ReplyType.EVERYONE, false, null);

		return createPost(createUser(), ReplyType.EVERYONE, false, parent);
	}

	public PostView createPostView() {
		PostEntity post1 = createPost();

		return postMapper.toPostView(post1);
	}

	public Page<PostView> createPostViewPage(Pageable pageable) {
		PostView post1 = createPostView();
		PostView post2 = createPostView();
		PostView post3 = createPostView();
		PostView post4 = createPostView();

		List<PostView> list = List.of(post1, post2, post3, post4);

		return new PageImpl<>(list, pageable, list.size());
	}

	public UserView createUserView() {
		return userMapper.toUserView(createUser());
	}

	public List<MediaView> createMediaViews(Iterable<Long> postIds) {
		List<MediaView> list = new LinkedList<>();
		for (Long id : postIds) {
			list.add(MediaView.builder().id(randomId()).url("example.com/image.jpg").postId(id).build());
		}
		return list;
	}

	public InteractionsView createInteractionsView(Long id) {
		return new InteractionsView(id, randomInt(), randomInt(), randomInt());
	}

	public List<InteractionsView> createInteractionsView(Iterable<Long> postIds) {
		List<InteractionsView> list = new LinkedList<>();
		for (Long id : postIds) {
			list.add(new InteractionsView(id, randomInt(), randomInt(), randomInt()));
		}
		return list;
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
