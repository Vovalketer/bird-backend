package com.gray.bird.post;

import org.springframework.context.annotation.Import;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import com.gray.bird.exception.GlobalExceptionHandler;
import com.gray.bird.exception.ResourceNotFoundException;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.post.dto.request.PostContentRequest;
import com.gray.bird.post.dto.request.PostRequest;
import com.gray.bird.post.event.PostEventPublisher;
import com.gray.bird.utils.TestUtils;
import com.gray.bird.utils.TestUtilsFactory;

@Import(GlobalExceptionHandler.class)
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
	@Mock
	private PostRepository repository;
	@Mock
	private PostMapper mapper;
	@Mock
	private PostEventPublisher postEventPublisher;
	@InjectMocks
	private PostService service;
	private TestUtils testUtils = TestUtilsFactory.createTestUtils();

	@Test
	void createValidPostWithoutMedia() {
		PostEntity post = testUtils.createPost();
		PostRequest req = new PostRequest(new PostContentRequest(post.getText(), post.getReplyAudience()));
		PostProjection projection = new PostProjection(post.getId(),
			post.getUserId(),
			post.getText(),
			post.isDeleted(),
			post.isHasMedia(),
			post.getReplyAudience(),
			null,
			post.getCreatedAt());

		Mockito.when(repository.save(Mockito.any(PostEntity.class))).thenReturn(post);
		Mockito.when(mapper.toPostProjection(Mockito.any(PostEntity.class))).thenReturn(projection);

		PostProjection postResult = service.createPost(req, post.getUserId());

		Assertions.assertThat(postResult).isNotNull();
		Assertions.assertThat(postResult.id()).isEqualTo(post.getId());
		Assertions.assertThat(postResult.text()).isEqualTo(post.getText());
		Assertions.assertThat(postResult.userId()).isEqualTo(post.getUserId());
		Assertions.assertThat(postResult.deleted()).isEqualTo(post.isDeleted());
		Assertions.assertThat(postResult.replyAudience()).isEqualTo(post.getReplyAudience());
		Assertions.assertThat(postResult.createdAt()).isEqualTo(post.getCreatedAt());
		// ensure that the parent post is null, therefore it isnt a reply
		Assertions.assertThat(post.getParentPost()).isNull();
		Assertions.assertThat(post.getParentPostId()).isNull();
		Assertions.assertThat(postResult.parentPostId()).isEqualTo(post.getParentPostId());

		Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(PostEntity.class));
		Mockito.verify(mapper, Mockito.times(1)).toPostProjection(Mockito.any(PostEntity.class));
		Mockito.verify(postEventPublisher, Mockito.times(1))
			.publishPostCreatedEvent(post.getUserId(), post.getId());
	}

	@Test
	void createValidReplyWithoutMedia() {
		PostEntity post = testUtils.createReply();
		PostRequest req = new PostRequest(new PostContentRequest(post.getText(), post.getReplyAudience()));
		PostProjection projection = new PostProjection(post.getId(),
			post.getUserId(),
			post.getText(),
			post.isDeleted(),
			post.isHasMedia(),
			post.getReplyAudience(),
			post.getParentPostId(),
			post.getCreatedAt());

		Mockito.when(repository.save(Mockito.any(PostEntity.class))).thenReturn(post);
		Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(post.getParentPost()));
		Mockito.when(mapper.toPostProjection(Mockito.any(PostEntity.class))).thenReturn(projection);

		PostProjection postResult = service.createReply(req, post.getParentPostId(), post.getUserId());

		Assertions.assertThat(postResult).isNotNull();
		Assertions.assertThat(postResult.id()).isEqualTo(post.getId());
		Assertions.assertThat(postResult.text()).isEqualTo(post.getText());
		Assertions.assertThat(postResult.userId()).isEqualTo(post.getUserId());
		Assertions.assertThat(postResult.deleted()).isEqualTo(post.isDeleted());
		Assertions.assertThat(postResult.replyAudience()).isEqualTo(post.getReplyAudience());
		Assertions.assertThat(postResult.createdAt()).isEqualTo(post.getCreatedAt());

		// ensure that the parent post is not null, therefore it is a reply
		Assertions.assertThat(post.getParentPost()).isNotNull();
		Assertions.assertThat(postResult.parentPostId()).isEqualTo(post.getParentPostId());

		Mockito.verify(repository, Mockito.times(1)).findById(Mockito.anyLong());
		Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(PostEntity.class));
		Mockito.verify(mapper, Mockito.times(1)).toPostProjection(Mockito.any(PostEntity.class));
	}

	@Test
	void getPostByPostId() {
		PostEntity post = testUtils.createPost();
		Long id = post.getId();

		Mockito.when(repository.findById(id)).thenReturn(Optional.of(post));

		PostEntity resPost = service.getByPostId(id);

		Assertions.assertThat(resPost).isNotNull();
		Assertions.assertThat(resPost.getId()).isEqualTo(id);
	}

	@Test
	void failToGetPostById() {
		Mockito.when(repository.findById(Mockito.anyLong())).thenThrow(new ResourceNotFoundException());
		Assertions.assertThatThrownBy(() -> service.getByPostId(Mockito.anyLong()))
			.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void testSavePost() {
		PostEntity post = testUtils.createPost();
		Mockito.when(repository.save(Mockito.any(PostEntity.class))).thenReturn(post);

		PostEntity savedPost = service.savePost(post);

		Assertions.assertThat(savedPost).isNotNull();
	}
}
