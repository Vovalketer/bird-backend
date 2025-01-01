package com.gray.bird.post;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.gray.bird.post.dto.PostProjection;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class PostQueryServiceTest {

	@Autowired
	private PostRepository postRepository;

	@Test
	void testGetAllPostsById() {

	}

	@Test
	void testGetPostById() {

	}

	@Test
	void testGetPostProjectionById() {
		Optional<PostProjection> post = postRepository.findById(2L, PostProjection.class);
		Assertions.assertThat(post.isPresent()).isTrue();
	}
}
