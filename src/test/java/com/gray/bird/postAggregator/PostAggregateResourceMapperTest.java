package com.gray.bird.postAggregator;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.gray.bird.post.dto.PostResource;
import com.gray.bird.utils.TestUtils;

@ExtendWith(SpringExtension.class)
public class PostAggregateResourceMapperTest {
	private PostAggregateResourceMapper mapper = new PostAggregateResourceMapper();
	private TestUtils testUtils = new TestUtils();

	@Test
	void testToResource() {
		PostAggregate postAggregate = testUtils.createReplyPostAggregateWithoutMedia(101L);
		PostResource resource = mapper.toResource(postAggregate);

		Assertions.assertThat(resource.getId()).isEqualTo(postAggregate.post().id());
		Assertions.assertThat(resource.getAttributes().text()).isEqualTo(postAggregate.post().text());
		Assertions.assertThat(resource.getAttributes().replyType())
			.isEqualTo(postAggregate.post().replyType());
		Assertions.assertThat(resource.getAttributes().createdAt())
			.isEqualTo(postAggregate.post().createdAt());
		Assertions.assertThat(resource.getRelationships().getUser().getData().getId())
			.isEqualTo(postAggregate.post().userId().toString());
		Assertions.assertThat(resource.getRelationships().getParentPost().getData().getId())
			.isEqualTo(postAggregate.post().parentPostId());
		Assertions.assertThat(resource.getMetadata().getMetadata("interactions").get()).isNotNull();
		// TODO: media
	}
}
