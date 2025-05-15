package com.gray.bird.media.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

import com.gray.bird.common.ResourceType;
import com.gray.bird.common.json.RelationshipToOne;

@AllArgsConstructor
@Getter
public class MediaRelationships {
	RelationshipToOne<UUID> user;
	RelationshipToOne<Long> post;

	public MediaRelationships(UUID userId, Long postId) {
		this.user = new RelationshipToOne<UUID>(ResourceType.USERS.getType(), userId);
		this.post = new RelationshipToOne<Long>(ResourceType.POSTS.getType(), postId);
	}
}
