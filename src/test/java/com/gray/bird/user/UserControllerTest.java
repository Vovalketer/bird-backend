package com.gray.bird.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gray.bird.auth.AuthService;
import com.gray.bird.common.PaginationMetadata;
import com.gray.bird.common.jsonApi.ResourceCollectionAggregate;
import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceResponseFactory;
import com.gray.bird.common.jsonApi.ResourceSingleAggregate;
import com.gray.bird.common.utils.MetadataUtils;
import com.gray.bird.post.PostResourceMapper;
import com.gray.bird.post.PostService;
import com.gray.bird.postAggregator.PostAggregate;
import com.gray.bird.postAggregator.PostAggregateResourceMapper;
import com.gray.bird.postAggregator.PostAggregatorService;
import com.gray.bird.user.dto.UserCreationRequest;
import com.gray.bird.user.dto.UserProjection;
import com.gray.bird.user.follow.FollowService;
import com.gray.bird.utils.TestResources;

@SpringJUnitConfig
public class UserControllerTest {
	private TestResources testResources = new TestResources();

	@Mock
	private UserService userService;
	@Mock
	private PostAggregatorService postAggregatorService;
	@Mock
	private PostService postService;
	@Mock
	private FollowService followService;
	@Mock
	private AuthService authService;
	@Mock
	private UserResourceMapper userResourceMapper;
	@Mock
	private PostResourceMapper postResourceMapper;
	@Mock
	private PostAggregateResourceMapper postAggregateResourceMapper;
	@Mock
	private ResourceResponseFactory responseFactory;
	@Mock
	private MetadataUtils metadataUtils;

	@InjectMocks
	private UserController userController;

	@SuppressWarnings("unchecked")
	@Test
	void testGetUserPosts() throws Exception {
		String username = "testUser";
		int pageNumber = 0;
		int pageSize = 10;
		// Mock ResourceCollectionAggregate
		ResourceCollectionAggregate response = testResources.createPostCollectionAggregate(5);

		// Mock userService
		Mockito.when(userService.getUserIdByUsername(username)).thenReturn(UUID.randomUUID());

		// get post ids
		Page<Long> postIds = Mockito.mock(Page.class);
		Mockito.when(postService.getPostIdsByUserId(Mockito.any(UUID.class), Mockito.any(Pageable.class)))
			.thenReturn(postIds);

		// mock posts, get and map them
		List<PostAggregate> posts = response.getData()
										.stream()
										.map(p -> Mockito.mock(PostAggregate.class))
										.collect(Collectors.toList());
		Mockito.when(postAggregatorService.getPosts(Mockito.anyCollection())).thenReturn(posts);

		Mockito.when(postAggregateResourceMapper.toResource(Mockito.any(PostAggregate.class)))
			.thenReturn(Mockito.mock(ResourceData.class));

		// create response
		Mockito.when(responseFactory.createResponse(Mockito.anyList())).thenReturn(response);

		// add pagination metadata
		PaginationMetadata paginationMetadata = new PaginationMetadata(
			posts.size(), posts.size(), true, true, false, PageRequest.of(pageNumber, pageSize));
		Mockito.when(metadataUtils.extractPaginationMetadata(Mockito.any(Page.class)))
			.thenReturn(paginationMetadata);

		ResponseEntity<ResourceCollectionAggregate> userPosts =
			userController.getUserPosts(username, pageNumber, pageSize);

		Assertions.assertThat(userPosts.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(userPosts.getBody()).isEqualTo(response);
		Assertions.assertThat(userPosts.getBody().getData().size()).isEqualTo(response.getData().size());

		Mockito.verify(postService).getPostIdsByUserId(Mockito.any(UUID.class), Mockito.any(Pageable.class));
		Mockito.verify(postAggregateResourceMapper, Mockito.times(posts.size()))
			.toResource(Mockito.any(PostAggregate.class));
		Mockito.verify(responseFactory).createResponse(Mockito.anyList());
		Mockito.verify(metadataUtils).extractPaginationMetadata(postIds);
	}

	@Test
	void registerValidData() throws Exception {
		UserCreationRequest data =
			new UserCreationRequest("username", "some@email.com", "securepassword", "handle");
		UserProjection user = Mockito.mock(UserProjection.class);
		Mockito.when(userService.createUser(data)).thenReturn(user);

		ResourceData userResource = Mockito.mock(ResourceData.class);
		Mockito.when(userResourceMapper.toResource(Mockito.any(UserProjection.class)))
			.thenReturn(userResource);

		ResourceSingleAggregate response = testResources.createUserSingleAggregate();
		Mockito.when(responseFactory.createResponse(userResource)).thenReturn(response);

		ResponseEntity<ResourceSingleAggregate> registerResponse = userController.register(data);
		Assertions.assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		Assertions.assertThat(registerResponse.getBody()).isEqualTo(response);

		Mockito.verify(userService).createUser(data);
		Mockito.verify(userResourceMapper).toResource(user);
		Mockito.verify(responseFactory).createResponse(userResource);
	}

	@Test
	void shouldReturnUserProfile() throws Exception {
		String username = "testUser";

		UserProjection user = Mockito.mock(UserProjection.class);
		Mockito.when(userService.getUserByUsername(Mockito.anyString())).thenReturn(user);

		ResourceData userResource = Mockito.mock(ResourceData.class);
		Mockito.when(userResourceMapper.toResource(user)).thenReturn(userResource);

		ResourceSingleAggregate response = testResources.createUserSingleAggregate();
		Mockito.when(responseFactory.createResponse(userResource)).thenReturn(response);

		ResponseEntity<ResourceSingleAggregate> userProfileResponse = userController.getUserProfile(username);

		Assertions.assertThat(userProfileResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(userProfileResponse.getBody()).isEqualTo(response);

		Mockito.verify(userService).getUserByUsername(username);
		Mockito.verify(userResourceMapper).toResource(user);
		Mockito.verify(responseFactory).createResponse(userResource);
	}

	@Test
	void shouldFollowUser() {
		String username = "testUser";
		UUID userId = UUID.randomUUID();

		Mockito.doNothing().when(followService).followUser(userId, username);

		ResponseEntity<Void> followResponse = userController.follow(username, userId);
		Assertions.assertThat(followResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(followResponse.getBody()).isNull();
	}

	@Test
	void shouldUnfollowUser() {
		String username = "testUser";
		UUID userId = UUID.randomUUID();

		Mockito.doNothing().when(followService).unfollowUser(userId, username);
		ResponseEntity<Void> unfollowResponse = userController.unfollow(username, userId);
		Assertions.assertThat(unfollowResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(unfollowResponse.getBody()).isNull();
	}
}
