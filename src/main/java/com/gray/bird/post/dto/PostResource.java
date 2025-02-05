package com.gray.bird.post.dto;

import com.gray.bird.common.ResourceType;
import com.gray.bird.common.json.ResourceData;

public class PostResource extends ResourceData<Long, PostAttributes, PostRelationships> {
	public PostResource(Long id, PostAttributes attributes, PostRelationships relationships) {
		super(type(), id, attributes, relationships);
	}

	public static String type() {
		return ResourceType.POSTS.getType();
	}
}
