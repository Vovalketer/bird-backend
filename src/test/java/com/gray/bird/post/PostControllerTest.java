package com.gray.bird.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.gray.bird.exception.GlobalExceptionHandler;
import com.gray.bird.media.dto.MediaProjection;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.postAggregator.PostAggregate;
import com.gray.bird.postAggregator.PostAggregatorService;
import com.gray.bird.postAggregator.PostResourceConverter;
import com.gray.bird.postAggregator.dto.PostInteractions;
import com.gray.bird.user.UserResourceConverter;

@WebMvcTest(controllers = PostController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class,
	useDefaultFilters = false)
@ContextConfiguration(classes = PostController.class)
@Import(GlobalExceptionHandler.class)
public class PostControllerTest {
	@Autowired
	MockMvc mockMvc;
	@MockBean
	PostAggregatorService postAggregatorService;
	@MockBean
	PostResourceConverter postResourceConverter;
	@MockBean
	UserResourceConverter userResourceConverter;

	void testOutput() throws Exception {
		PostProjection post = new PostProjection(
			1L, UUID.randomUUID(), "testText", false, ReplyType.EVERYONE, null, LocalDateTime.now());
		List<MediaProjection> media = new ArrayList<>();
		Optional<PostInteractions> interactions = Optional.of(new PostInteractions(1L, 3L, 9L, 3L));
		PostAggregate postAggregate = new PostAggregate(post, media, interactions);

		Mockito.when(postAggregatorService.getPost(ArgumentMatchers.anyLong())).thenReturn(postAggregate);
		mockMvc.perform(MockMvcRequestBuilders.get("api/posts/1").contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk());
	}
}
