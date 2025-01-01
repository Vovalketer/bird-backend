package com.gray.bird.post;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.gray.bird.post.PostCommandService;
import com.gray.bird.post.PostEntity;
import com.gray.bird.post.PostRepository;
import com.gray.bird.utils.TestUtils;

@Import(GlobalExceptionHandler.class)
@ExtendWith(MockitoExtension.class)
@Deprecated
public class PostServiceTest {
	@Mock
	private PostRepository repository;
	@InjectMocks
	private PostCommandService service;
	@Autowired
	private TestUtils testUtils;

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
