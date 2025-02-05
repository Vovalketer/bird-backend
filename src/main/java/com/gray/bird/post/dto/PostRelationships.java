package com.gray.bird.post.dto;

import org.springframework.lang.Nullable;

import lombok.Getter;

import jakarta.validation.constraints.NotNull;

import java.util.Collection;

import com.gray.bird.common.ResourceType;
import com.gray.bird.common.json.RelationshipToMany;
import com.gray.bird.common.json.RelationshipToOne;
import com.gray.bird.common.json.ResourceIdentifier;

@Getter
public class PostRelationships {
	RelationshipToOne<String> user;
	RelationshipToOne<Long> parentPost; // might be better off as optional, to check later
	RelationshipToMany<Long> media;

	public PostRelationships(
		@NotNull RelationshipToOne<String> user, @Nullable RelationshipToOne<Long> parent) {
		this.user = user;
		this.parentPost = parent;
	}

	public PostRelationships(@NotNull String userId, @Nullable Long parentId) {
		this.user = new RelationshipToOne<>(new ResourceIdentifier<>(ResourceType.USERS.getType(), userId));
		if (parentId != null) {
			this.parentPost =
				new RelationshipToOne<>(new ResourceIdentifier<>(ResourceType.POSTS.getType(), parentId));
		}
	}

	public PostRelationships(@NotNull String userId, @Nullable Long parentId, Collection<Long> mediaIds) {
		this.user = new RelationshipToOne<>(new ResourceIdentifier<>(ResourceType.USERS.getType(), userId));
		if (parentId == null) {
			this.parent =
				new RelationshipToOne<>(new ResourceIdentifier<>(ResourceType.POSTS.getType(), parentId));
		}
		this.media = new RelationshipToMany<>(ResourceType.MEDIA.getType(), mediaIds);
	}
}
