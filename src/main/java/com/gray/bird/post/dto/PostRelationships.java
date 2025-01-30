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
	RelationshipToOne<String> author;
	RelationshipToOne<Long> parent; // might be better off as optional, to check later
	RelationshipToMany<Long> media;

	public PostRelationships(
		@NotNull RelationshipToOne<String> author, @Nullable RelationshipToOne<Long> parent) {
		this.author = author;
		this.parent = parent;
	}

	public PostRelationships(@NotNull String authorId, @Nullable Long parentId) {
		this.author =
			new RelationshipToOne<>(new ResourceIdentifier<>(ResourceType.USERS.getType(), authorId));
		if (parentId != null) {
			this.parent =
				new RelationshipToOne<>(new ResourceIdentifier<>(ResourceType.POSTS.getType(), parentId));
		}
	}

	public PostRelationships(@NotNull String authorId, @Nullable Long parentId, Collection<Long> mediaIds) {
		this.author =
			new RelationshipToOne<>(new ResourceIdentifier<>(ResourceType.USERS.getType(), authorId));
		if (parentId == null) {
			this.parent =
				new RelationshipToOne<>(new ResourceIdentifier<>(ResourceType.POSTS.getType(), parentId));
		}
		this.media = new RelationshipToMany<>(ResourceType.MEDIA.getType(), mediaIds);
	}
}
