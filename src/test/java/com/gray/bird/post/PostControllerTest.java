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

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gray.bird.common.jsonApi.ResourceCollectionAggregate;
import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceResponseFactory;
import com.gray.bird.common.jsonApi.ResourceSingleAggregate;
import com.gray.bird.common.utils.MetadataUtils;
import com.gray.bird.post.dto.PostCreationRequest;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.postAggregator.PostAggregate;
import com.gray.bird.postAggregator.PostAggregateResourceMapper;
import com.gray.bird.postAggregator.PostAggregatorService;
import com.gray.bird.user.UserResourceMapper;
import com.gray.bird.user.UserService;
import com.gray.bird.user.dto.UserProjection;
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
	ResourceResponseFactory responseFactory;
	@Mock
	MetadataUtils metadataUtils;

	@InjectMocks
	PostController postController;

	TestUtils testUtils = new TestUtils();

	@Test
	void shouldReturnCreatedPostWhenPostIsSubmitted() {
		PostCreationRequest req = Mockito.mock(PostCreationRequest.class);
		UUID userId = UUID.randomUUID();

		PostProjection postProjection = Mockito.mock(PostProjection.class);
		ResourceData resourceData = Mockito.mock(ResourceData.class);
		ResourceSingleAggregate resourceSingleAggregate = Mockito.mock(ResourceSingleAggregate.class);

		Mockito.when(postService.createPost(req, userId)).thenReturn(postProjection);
		Mockito.when(postResourceMapper.toResource(postProjection)).thenReturn(resourceData);
		Mockito.when(responseFactory.createResponse(resourceData)).thenReturn(resourceSingleAggregate);

		ResponseEntity<ResourceSingleAggregate> postResponse = postController.createPost(req, userId);

		Assertions.assertThat(postResponse.getStatusCode())
			.isEqualTo(ResponseEntity.status(201).build().getStatusCode());
		Assertions.assertThat(postResponse.getBody()).isEqualTo(resourceSingleAggregate);
		Mockito.verify(postService).createPost(req, userId);
		Mockito.verify(postResourceMapper).toResource(postProjection);
		Mockito.verify(responseFactory).createResponse(resourceData);
	}

	@Test
	void shouldReturnPostWhenItsRequested() {
		PostAggregate post = testUtils.createPostAggregateWithoutMedia();
		Long postId = post.post().id();
		UUID userId = post.post().userId();
		ResourceData postResourceData = Mockito.mock(ResourceData.class);
		Mockito.when(postAggregatorService.getPost(postId)).thenReturn(post);
		Mockito.when(postAggregateResourceMapper.toResource(post)).thenReturn(postResourceData);

		UserProjection userProjection = testUtils.createUserProjection(userId);
		ResourceData userResourceData = Mockito.mock(ResourceData.class);
		Mockito.when(userService.getUserById(userId)).thenReturn(userProjection);
		Mockito.when(userResourceMapper.toResource(userProjection)).thenReturn(userResourceData);

		ResourceSingleAggregate response = Mockito.mock(ResourceSingleAggregate.class);
		Mockito.when(responseFactory.createResponse(postResourceData, userResourceData)).thenReturn(response);

		ResponseEntity<ResourceSingleAggregate> postResponse = postController.getPost(postId);

		Assertions.assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(postResponse.getBody()).isEqualTo(response);
		Mockito.verify(postAggregatorService).getPost(postId);
		Mockito.verify(postAggregateResourceMapper).toResource(post);
		Mockito.verify(userService).getUserById(userId);
		Mockito.verify(userResourceMapper).toResource(userProjection);
		Mockito.verify(responseFactory).createResponse(postResourceData, userResourceData);
	}

	@Test
	void shouldReturnRepliesWhenTheyAreRequested() {
		Long postId = 1L;
		int pageNumber = 0;
		int pageSize = 10;
		List<PostAggregate> replies = testUtils.createReplyPostAggregateWithoutMedia(postId, 3);

		// create page based on the replies we have
		Page<Long> replyIds =
			new PageImpl<>(replies.stream().map(p -> p.post().id()).collect(Collectors.toList()),
				PageRequest.of(pageNumber, pageSize),
				replies.size());

		Mockito.when(postService.getReplyIds(Mockito.eq(postId), Mockito.any(Pageable.class)))
			.thenReturn(replyIds);
		Mockito.when(postAggregatorService.getPosts(Mockito.any(Collection.class))).thenReturn(replies);
		ResourceData replyResource = Mockito.mock(ResourceData.class);
		Mockito.when(postAggregateResourceMapper.toResource(Mockito.any(PostAggregate.class)))
			.thenReturn(replyResource);

		// create users based on the posts we have
		List<UserProjection> users = replies.stream()
										 .map(r -> testUtils.createUserProjection(r.post().userId()))
										 .collect(Collectors.toList());
		Mockito.when(userService.getAllUsersById(Mockito.any(Iterable.class))).thenReturn(users);
		ResourceData userResource = Mockito.mock(ResourceData.class);
		Mockito.when(userResourceMapper.toResource(Mockito.any(UserProjection.class)))
			.thenReturn(userResource);

		ResourceCollectionAggregate response = Mockito.mock(ResourceCollectionAggregate.class);
		Mockito.when(responseFactory.createResponse(Mockito.anyList(), Mockito.anyList()))
			.thenReturn(response);

		ResponseEntity<ResourceCollectionAggregate> repliesResponse =
			postController.getReplies(postId, pageNumber, pageSize);

		Assertions.assertThat(repliesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(repliesResponse.getBody()).isEqualTo(response);

		Mockito.verify(postService).getReplyIds(postId, PageRequest.of(pageNumber, pageSize));
		Mockito.verify(postAggregateResourceMapper, Mockito.times(replies.size()))
			.toResource(Mockito.any(PostAggregate.class));
		Mockito.verify(userResourceMapper, Mockito.times(users.size()))
			.toResource(Mockito.any(UserProjection.class));
		Mockito.verify(responseFactory).createResponse(Mockito.anyList(), Mockito.anyList());
	}

	@Test
	void shouldReturnCreatedReplyWhenReplyIsSubmitted() {
		Long postId = 1L;
		UUID userId = UUID.randomUUID();
		PostCreationRequest req = Mockito.mock(PostCreationRequest.class);
		PostProjection reply = testUtils.createReplyPostProjection(postId);
		ResourceData resourceData = Mockito.mock(ResourceData.class);
		ResourceSingleAggregate resourceSingleAggregate = Mockito.mock(ResourceSingleAggregate.class);

		Mockito.when(postService.createReply(req, postId, userId)).thenReturn(reply);
		Mockito.when(postResourceMapper.toResource(reply)).thenReturn(resourceData);
		Mockito.when(responseFactory.createResponse(resourceData)).thenReturn(resourceSingleAggregate);

		ResponseEntity<ResourceSingleAggregate> replyResponse = postController.postReply(postId, req, userId);

		Assertions.assertThat(replyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		Assertions.assertThat(replyResponse.getBody()).isEqualTo(resourceSingleAggregate);
		Mockito.verify(postResourceMapper).toResource(reply);
		Mockito.verify(responseFactory).createResponse(resourceData);
	}
}
