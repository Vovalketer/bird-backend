package com.gray.bird.postAggregator;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.UUID;

import com.gray.bird.post.ReplyAudience;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.post.dto.PostResource;
import com.gray.bird.postAggregator.dto.PostEngagement;
import com.gray.bird.postAggregator.dto.PostMetrics;
import com.gray.bird.postAggregator.dto.UserPostInteractions;
import com.gray.bird.utils.TestUtils;

@ExtendWith(SpringExtension.class)
public class PostAggregateResourceMapperTest {
	private PostAggregateResourceMapper mapper = new PostAggregateResourceMapper();
	private TestUtils testUtils = new TestUtils();

	@Test
	void testToResource() {
		UUID userId = UUID.randomUUID();
		long postId = 101L;
		PostAggregate postAggregate = new PostAggregate(
			new PostProjection(
				postId, userId, "text", false, false, ReplyAudience.PUBLIC, 102L, LocalDateTime.now()),
			null,
			new PostEngagement(postId,
				new PostMetrics(100L, 100L, 100L),
				new UserPostInteractions(true, LocalDateTime.now(), true, LocalDateTime.now())));
		PostResource resource = mapper.toResource(postAggregate);

		Assertions.assertThat(resource.getId()).isEqualTo(postAggregate.post().id());
		Assertions.assertThat(resource.getAttributes().text()).isEqualTo(postAggregate.post().text());
		Assertions.assertThat(resource.getAttributes().replyAudience())
			.isEqualTo(postAggregate.post().replyAudience());
		Assertions.assertThat(resource.getAttributes().createdAt())
			.isEqualTo(postAggregate.post().createdAt());
		Assertions.assertThat(resource.getRelationships().getUser().getData().getId())
			.isEqualTo(postAggregate.post().userId());
		Assertions.assertThat(resource.getRelationships().getParentPost().getData().getId())
			.isEqualTo(postAggregate.post().parentPostId());
		Assertions.assertThat(resource.getMetadata().getMetadata("metrics").get()).isNotNull();
		Assertions.assertThat(resource.getMetadata().getMetadata("userInteractions").get()).isNotNull();
		// TODO: media
	}
}
