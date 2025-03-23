package com.gray.bird.post;

import org.springframework.stereotype.Component;

import com.gray.bird.common.json.ResourceMapper;
import com.gray.bird.post.dto.PostAttributes;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.post.dto.PostRelationships;
import com.gray.bird.post.dto.PostResource;

@Component
public class PostResourceMapper implements ResourceMapper<PostProjection, PostResource> {
	@Override
	public PostResource toResource(PostProjection data) {
		PostAttributes postAttributes = getPostAttributes(data);
		PostRelationships relationships = new PostRelationships(data.userId(), data.parentPostId());
		PostResource resource = new PostResource(data.id(), postAttributes, relationships);

		return resource;
	}

	private PostAttributes getPostAttributes(PostProjection data) {
		return new PostAttributes(data.text(), data.replyType(), data.createdAt());
	}
}
