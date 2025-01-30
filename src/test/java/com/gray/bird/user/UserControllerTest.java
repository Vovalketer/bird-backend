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

import com.gray.bird.auth.AuthService;
import com.gray.bird.common.JsonApiResponse;
import com.gray.bird.common.PaginationMetadata;
import com.gray.bird.common.utils.JsonApiResponseFactory;
import com.gray.bird.common.utils.MetadataUtils;
import com.gray.bird.post.PostResourceMapper;
import com.gray.bird.post.PostService;
import com.gray.bird.post.dto.PostResource;
import com.gray.bird.postAggregator.PostAggregate;
import com.gray.bird.postAggregator.PostAggregateResourceMapper;
import com.gray.bird.postAggregator.PostAggregatorService;
import com.gray.bird.user.dto.UserCreationRequest;
import com.gray.bird.user.dto.UserProjection;
import com.gray.bird.user.dto.UserResource;
import com.gray.bird.user.follow.FollowService;

@SpringJUnitConfig
public class UserControllerTest {
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
	private JsonApiResponseFactory responseFactory;
	@Mock
	private MetadataUtils metadataUtils;

	@InjectMocks
	private UserController userController;

	@Test
	void getUserPostsShouldReturnEmptyListWhenNoPosts() {
	}

	@Test
	void getUserPostsShouldReturnPaginatedResponse() {
		String username = "testUser";
		UUID userId = UUID.randomUUID();
		int pageNumber = 0;
		int pageSize = 10;
		Mockito.when(userService.getUserIdByUsername(username)).thenReturn(userId);

		@SuppressWarnings("unchecked")
		Page<Long> postIds = Mockito.mock(Page.class);
		Mockito.when(postService.getPostIdsByUserId(Mockito.eq(userId), Mockito.any(Pageable.class)))
			.thenReturn(postIds);

		PostAggregate postAggregate = Mockito.mock(PostAggregate.class);
		Mockito.when(postAggregatorService.getPosts(Mockito.anyCollection()))
			.thenReturn(List.of(postAggregate));

		List<PostResource> postResources = List.of(Mockito.mock(PostResource.class));
		Mockito.when(postAggregateResourceMapper.toResource(postAggregate))
			.thenReturn(postResources.get(pageNumber));

		@SuppressWarnings("unchecked")
		JsonApiResponse<List<PostResource>> response = Mockito.mock(JsonApiResponse.class);
		Mockito.when(responseFactory.createResponse(postResources)).thenReturn(response);

		PaginationMetadata paginationMetadata = Mockito.mock(PaginationMetadata.class);
		Mockito.when(metadataUtils.extractPaginationMetadata(postIds)).thenReturn(paginationMetadata);

		ResponseEntity<JsonApiResponse<List<PostResource>>> userPosts =
			userController.getUserPosts(username, pageNumber, pageSize);

		Assertions.assertThat(userPosts.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(userPosts.getBody()).isEqualTo(response);

		Mockito.verify(postService).getPostIdsByUserId(userId, PageRequest.of(pageNumber, pageSize));
		Mockito.verify(postAggregatorService).getPosts(postIds.getContent());
		Mockito.verify(postAggregateResourceMapper).toResource(postAggregate);
		Mockito.verify(responseFactory).createResponse(postResources);
		Mockito.verify(metadataUtils).extractPaginationMetadata(postIds);
	}

	@Test
	void registerValidData() throws Exception {
		UserCreationRequest data =
			new UserCreationRequest("username", "some@email.com", "securepassword", "handle");
		UserProjection user = Mockito.mock(UserProjection.class);
		Mockito.when(userService.createUser(data)).thenReturn(user);

		UserResource userResource = Mockito.mock(UserResource.class);
		Mockito.when(userResourceMapper.toResource(Mockito.any(UserProjection.class)))
			.thenReturn(userResource);

		@SuppressWarnings("unchecked")
		JsonApiResponse<UserResource> response = Mockito.mock(JsonApiResponse.class);
		Mockito.when(responseFactory.createResponse(userResource)).thenReturn(response);

		ResponseEntity<JsonApiResponse<UserResource>> registerResponse = userController.register(data);
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

		UserResource userResource = Mockito.mock(UserResource.class);
		Mockito.when(userResourceMapper.toResource(user)).thenReturn(userResource);

		@SuppressWarnings("unchecked")
		JsonApiResponse<UserResource> response = Mockito.mock(JsonApiResponse.class);
		Mockito.when(responseFactory.createResponse(userResource)).thenReturn(response);

		ResponseEntity<JsonApiResponse<UserResource>> userProfileResponse =
			userController.getUserProfile(username);

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
