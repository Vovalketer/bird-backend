package com.gray.bird.post;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.gray.bird.common.ResourceType;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.post.dto.PostResource;
import com.gray.bird.utils.TestUtils;

@ExtendWith(SpringExtension.class)
public class PostResourceMapperTest {
	private PostResourceMapper postResourceMapper = new PostResourceMapper();
	private TestUtils testUtils = new TestUtils();

	@Test
	void testToResource() {
		Long parentId = 101L;
		PostProjection postProjection = testUtils.createReplyPostProjection(parentId);
		PostResource resource = postResourceMapper.toResource(postProjection);

		Assertions.assertThat(resource).isNotNull();
		// id
		Assertions.assertThat(resource.getType()).isEqualTo(ResourceType.POSTS.getType());
		Assertions.assertThat(resource.getId()).isEqualTo(postProjection.id());
		// attributes
		Assertions.assertThat(resource.getAttributes()).isNotNull();
		Assertions.assertThat(resource.getAttributes().text()).isEqualTo(postProjection.text());
		Assertions.assertThat(resource.getAttributes().createdAt()).isEqualTo(postProjection.createdAt());
		Assertions.assertThat(resource.getAttributes().replyAudience())
			.isEqualTo(postProjection.replyAudience());
		// relationships
		Assertions.assertThat(resource.getRelationships()).isNotNull();
		Assertions.assertThat(resource.getRelationships().getUser()).isNotNull();
		Assertions.assertThat(resource.getRelationships().getUser().getData().getId())
			.isEqualTo(postProjection.userId());
		Assertions.assertThat(resource.getRelationships().getUser().getData().getType())
			.isEqualTo(ResourceType.USERS.getType());
		Assertions.assertThat(resource.getRelationships().getParentPost().getData().getId())
			.isEqualTo(postProjection.parentPostId());
		Assertions.assertThat(resource.getRelationships().getParentPost().getData().getType())
			.isEqualTo(ResourceType.POSTS.getType());
		// TODO: media relationship
	}
}
