package com.gray.bird.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gray.bird.common.JsonApiResponse;
import com.gray.bird.common.ResourcePaths;
import com.gray.bird.common.utils.JsonApiResponseFactory;
import com.gray.bird.common.utils.MetadataUtils;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.post.dto.PostResource;
import com.gray.bird.post.dto.request.PostContentRequest;
import com.gray.bird.post.dto.request.PostRequest;
import com.gray.bird.post.mapper.PostRequestMapper;
import com.gray.bird.postAggregator.PostAggregate;
import com.gray.bird.postAggregator.PostAggregateResourceMapper;
import com.gray.bird.postAggregator.PostAggregatorService;
import com.gray.bird.user.UserResourceMapper;
import com.gray.bird.user.UserService;
import com.gray.bird.user.dto.UserProjection;
import com.gray.bird.user.dto.UserResource;
import com.gray.bird.utils.TestPostFactory;
import com.gray.bird.utils.TestUtils;

@ExtendWith(SpringExtension.class)
public class PostControllerTest {
	@Mock
	UserService userService;
	@Mock
	PostService postService;
	@Mock
	PostAggregatorService postAggregatorService;
	@Mock
	PostAggregateResourceMapper postAggregateResourceMapper;
	@Mock
	PostResourceMapper postResourceMapper;
	@Mock
	UserResourceMapper userResourceMapper;
	@Mock
	PostRequestMapper postCreationRequestMapper;
	@Mock
	JsonApiResponseFactory responseFactory;
	@Mock
	MetadataUtils metadataUtils;

	@InjectMocks
	PostController postController;

	TestUtils testUtils = new TestUtils();

	@Test
	void shouldReturnCreatedPostWhenPostIsSubmitted() {
		UUID userId = UUID.randomUUID();
		Long newPostId = 1L;
		PostContentRequest contentReq = TestPostFactory.postContentRequest();

		PostRequest request = TestPostFactory.postCreationRequestWithoutMedia();
		Mockito.when(postCreationRequestMapper.toPostCreationRequest(contentReq, null, null))
			.thenReturn(request);

		PostProjection postProjection = TestPostFactory.postProjection(newPostId, userId);
		Mockito.when(postService.createPost(request, userId)).thenReturn(postProjection);

		PostResource postResource = TestPostFactory.postResource(newPostId, userId);
		Mockito.when(postResourceMapper.toResource(postProjection)).thenReturn(postResource);

		JsonApiResponse<PostResource> response = new JsonApiResponse<PostResource>(postResource);
		Mockito.when(responseFactory.createResponse(postResource)).thenReturn(response);

		ResponseEntity<JsonApiResponse<PostResource>> postResponse =
			postController.createPost(contentReq, null, null, userId);

		Mockito.verify(postCreationRequestMapper).toPostCreationRequest(contentReq, null, null);
		Mockito.verify(postService).createPost(request, userId);
		Mockito.verify(postResourceMapper).toResource(postProjection);
		Mockito.verify(responseFactory).createResponse(postResource);
		Assertions.assertThat(postResponse.getStatusCode())
			.isEqualTo(ResponseEntity.status(201).build().getStatusCode());
		Assertions.assertThat(postResponse.getBody()).isEqualTo(response);
		Assertions.assertThat(postResponse.getHeaders().getLocation())
			.isEqualTo(URI.create(ResourcePaths.POSTS + "/" + response.getData().getId()));
	}

	@Test
	void shouldReturnPostWhenItsRequested() {
		UUID nullUserId = null;
		PostAggregate post = testUtils.createPostAggregateWithoutMedia();
		Long postId = post.post().id();
		UUID postUserId = post.post().userId();
		PostResource postResource = Mockito.mock(PostResource.class);
		Mockito.when(postAggregatorService.getPost(postId, nullUserId)).thenReturn(post);
		Mockito.when(postAggregateResourceMapper.toResource(post)).thenReturn(postResource);

		UserProjection userProjection = testUtils.createUserProjection(postUserId);
		UserResource userResource = Mockito.mock(UserResource.class);
		Mockito.when(userService.getUserById(postUserId)).thenReturn(userProjection);
		Mockito.when(userResourceMapper.toResource(userProjection)).thenReturn(userResource);

		@SuppressWarnings("unchecked")
		JsonApiResponse<PostResource> response = Mockito.mock(JsonApiResponse.class);
		Mockito.when(responseFactory.createResponse(postResource)).thenReturn(response);

		ResponseEntity<JsonApiResponse<PostResource>> postResponse =
			postController.getPost(postId, nullUserId);

		Assertions.assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(postResponse.getBody()).isEqualTo(response);
		Mockito.verify(postAggregatorService).getPost(postId, nullUserId);
		Mockito.verify(postAggregateResourceMapper).toResource(post);
		Mockito.verify(userService).getUserById(postUserId);
		Mockito.verify(userResourceMapper).toResource(userProjection);
		Mockito.verify(responseFactory).createResponse(postResource);
	}

	@SuppressWarnings("unchecked")
	@Test
	void shouldReturnRepliesWhenTheyAreRequested() {
		UUID userId = UUID.randomUUID();
		Long postId = 1L;
		int page = 0;
		int limit = 10;
		List<PostAggregate> replies = testUtils.createReplyPostAggregateWithoutMedia(postId, 3);

		// create page based on the replies we have
		Page<Long> replyIds =
			new PageImpl<>(replies.stream().map(p -> p.post().id()).collect(Collectors.toList()),
				PageRequest.of(page, limit),
				replies.size());

		Mockito.when(postService.getReplyIds(Mockito.eq(postId), Mockito.any(Pageable.class)))
			.thenReturn(replyIds);
		Mockito.when(postAggregatorService.getPosts(Mockito.any(Collection.class), Mockito.eq(userId)))
			.thenReturn(replies);
		PostResource replyResource = Mockito.mock(PostResource.class);
		Mockito.when(postAggregateResourceMapper.toResource(Mockito.any(PostAggregate.class)))
			.thenReturn(replyResource);

		// create users based on the posts we have
		List<UserProjection> users = replies.stream()
										 .map(r -> testUtils.createUserProjection(r.post().userId()))
										 .collect(Collectors.toList());
		Mockito.when(userService.getAllUsersById(Mockito.any(Iterable.class))).thenReturn(users);
		UserResource userResource = Mockito.mock(UserResource.class);
		Mockito.when(userResourceMapper.toResource(Mockito.any(UserProjection.class)))
			.thenReturn(userResource);

		JsonApiResponse<List<Object>> response = Mockito.mock(JsonApiResponse.class);
		Mockito.when(responseFactory.createResponse(Mockito.anyList())).thenReturn(response);

		ResponseEntity<JsonApiResponse<List<PostResource>>> repliesResponse =
			postController.getReplies(postId, page, limit, userId);

		Assertions.assertThat(repliesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(repliesResponse.getBody()).isEqualTo(response);

		Mockito.verify(postService).getReplyIds(postId, PageRequest.of(page, limit));
		Mockito.verify(postAggregateResourceMapper, Mockito.times(replies.size()))
			.toResource(Mockito.any(PostAggregate.class));
		Mockito.verify(userResourceMapper, Mockito.times(users.size()))
			.toResource(Mockito.any(UserProjection.class));
		Mockito.verify(responseFactory).createResponse(Mockito.anyList());
	}

	@Test
	void shouldReturnCreatedReplyWhenReplyIsSubmitted() {
		Long replyingToPostId = 1L;
		Long newPostId = 2L;
		UUID userId = UUID.randomUUID();
		PostContentRequest content = TestPostFactory.postContentRequest();

		PostRequest request = TestPostFactory.postCreationRequestWithoutMedia();
		Mockito.when(postCreationRequestMapper.toPostCreationRequest(content, null, null))
			.thenReturn(request);

		PostProjection reply = TestPostFactory.postProjection(newPostId, userId);
		Mockito.when(postService.createReply(request, replyingToPostId, userId)).thenReturn(reply);

		PostResource replyResource = TestPostFactory.postResource(newPostId, userId);
		Mockito.when(postResourceMapper.toResource(reply)).thenReturn(replyResource);

		JsonApiResponse<PostResource> response = new JsonApiResponse<PostResource>(replyResource);
		Mockito.when(responseFactory.createResponse(replyResource)).thenReturn(response);

		ResponseEntity<JsonApiResponse<PostResource>> replyResponse =
			postController.createReply(replyingToPostId, content, null, null, userId);

		Mockito.verify(postCreationRequestMapper).toPostCreationRequest(content, null, null);
		Mockito.verify(postResourceMapper).toResource(reply);
		Mockito.verify(responseFactory).createResponse(replyResource);
		Assertions.assertThat(replyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		Assertions.assertThat(replyResponse.getBody()).isEqualTo(response);
		Assertions.assertThat(replyResponse.getHeaders().getLocation())
			.isEqualTo(URI.create(ResourcePaths.POSTS + "/" + response.getData().getId()));
	}
}
