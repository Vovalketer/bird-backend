package com.gray.bird.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceFactory;
import com.gray.bird.config.ObjectMapperConfig;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.utils.TestUtils;

@SpringBootTest(classes = {PostResourceMapper.class, ResourceFactory.class, ObjectMapperConfig.class})
public class PostResourceMapperTest {
	@Autowired
	private PostResourceMapper postResourceMapper;

	private TestUtils testUtils = new TestUtils();

	@Test
	void testToResource() {
		PostProjection postProjection = testUtils.createReplyPostProjection(1L);

		ResourceData resource = postResourceMapper.toResource(postProjection);

		Assertions.assertThat(resource).isNotNull();
		Assertions.assertThat(resource.getId()).isEqualTo(postProjection.id().toString());
		Assertions.assertThat(resource.getType()).isEqualTo("post");
		Assertions.assertThat(resource.getAttributes()).isNotNull();
		Assertions.assertThat(resource.getAttribute("text")).isEqualTo(postProjection.text());
		Assertions.assertThat(resource.getAttribute("replyType").toString())
			.isEqualTo(postProjection.replyType().name());
		Assertions.assertThat(resource.getAttribute("createdAt").toString());
		Assertions.assertThat(resource.getRelationships()).isNotNull();
		Assertions.assertThat(resource.getRelationshipToOne("parent")).isNotEmpty();
		Assertions.assertThat(resource.getRelationshipToOne("parent").get().getData().getId())
			.isEqualTo(postProjection.parentPostId().toString());
		Assertions.assertThat(resource.getRelationshipToOne("user")).isNotEmpty();
		Assertions.assertThat(resource.getRelationshipToOne("user").get().getData().getId())
			.isEqualTo(postProjection.userId().toString());
	}
}
