package com.gray.bird.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.gray.bird.common.jsonApi.ResourceCollectionAggregate;
import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceSingleAggregate;
import com.gray.bird.media.dto.MediaProjection;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.postAggregator.PostAggregate;
import com.gray.bird.postAggregator.PostResourceConverter;
import com.gray.bird.postAggregator.dto.PostInteractions;
import com.gray.bird.utils.TestUtils;
import com.gray.bird.utils.TestUtilsFactory;

@ExtendWith(SpringExtension.class)
public class PostResourceConverterTest {
	@Autowired
	private PostResourceConverter postResourceConverter;
	private TestUtils testUtils = TestUtilsFactory.createTestUtils();

	@Test
	void convertSinglePostToResource() {
		PostProjection post = new PostProjection(
			1L, UUID.randomUUID(), "testText", false, ReplyType.EVERYONE, null, LocalDateTime.now());
		List<MediaProjection> media = new ArrayList<>();
		Optional<PostInteractions> interactions = Optional.of(new PostInteractions(1L, 3L, 9L, 3L));
		PostAggregate postAggregate = new PostAggregate(post, media, interactions);

		ResourceSingleAggregate aggregate = postResourceConverter.toAggregate(postAggregate);

		Assertions.assertThat(aggregate).isNotNull();

		ResourceData data = aggregate.getData();
		Assertions.assertThat(data.getId()).isEqualTo(postAggregate.post().id().toString());
		System.out.println(aggregate.toString());
	}

	@Test
	void convertPostListToResource() {
		PostAggregate postAggregate1 = testUtils.createPostAggregate();
		PostAggregate postAggregate2 = testUtils.createPostAggregate();
		PostAggregate postAggregate3 = testUtils.createPostAggregate();
		List<PostAggregate> list = new ArrayList<>();
		list.add(postAggregate1);
		list.add(postAggregate2);
		list.add(postAggregate3);

		ResourceCollectionAggregate aggregate = postResourceConverter.toAggregate(list);

		Assertions.assertThat(aggregate).isNotNull();
		List<ResourceData> data = aggregate.getData();
		Assertions.assertThat(data).hasSize(list.size());
		Assertions.assertThat(data)
			.extracting(ResourceData::getId)
			.containsAll(list.stream().map(p -> p.post().id().toString()).toList());
		System.out.println(aggregate.toString());
	}
}
