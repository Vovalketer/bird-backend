package com.gray.bird.media.dto;

import com.gray.bird.common.ResourceType;
import com.gray.bird.common.json.RelationshipToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MediaRelationships {
	RelationshipToOne<Long> post;

	public MediaRelationships(Long postId) {
		this.post = new RelationshipToOne<Long>(ResourceType.POSTS.getType(), postId);
	}
}
