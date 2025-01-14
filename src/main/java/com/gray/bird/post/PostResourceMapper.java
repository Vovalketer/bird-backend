package com.gray.bird.post;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.gray.bird.common.jsonApi.RelationshipToOne;
import com.gray.bird.common.jsonApi.ResourceAttributes;
import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceDataMapper;
import com.gray.bird.common.jsonApi.ResourceFactory;
import com.gray.bird.common.jsonApi.ResourceIdentifier;
import com.gray.bird.post.dto.PostAttributes;
import com.gray.bird.post.dto.PostProjection;

@Component
@RequiredArgsConstructor
public class PostResourceMapper implements ResourceDataMapper<PostProjection> {
	private static final String PARENT = "parent";
	private static final String USER = "user";
	private static final String POST = "post";
	private final ResourceFactory resourceFactory;

	@Override
	public ResourceData toResource(PostProjection data) {
		PostAttributes postAttributes = getPostAttributes(data);
		ResourceAttributes attributes = resourceFactory.createAttributes(postAttributes);
		ResourceIdentifier identifier = resourceFactory.createIdentifier(POST, data.id().toString());
		ResourceData resource = resourceFactory.createData(identifier, attributes);
		if (data.parentPostId() != null) {
			resource.addRelationshipToOne(PARENT, createParentRelationship(data));
		}
		resource.addRelationshipToOne(USER, createUserRelationship(data));

		return resource;
	}

	private PostAttributes getPostAttributes(PostProjection data) {
		return new PostAttributes(data.text(), data.replyType(), data.createdAt());
	}

	private RelationshipToOne createParentRelationship(PostProjection post) {
		ResourceIdentifier identifier =
			resourceFactory.createIdentifier(POST, post.parentPostId().toString());
		return resourceFactory.createRelationshipToOne(identifier);
	}

	private RelationshipToOne createUserRelationship(PostProjection post) {
		ResourceIdentifier identifier = resourceFactory.createIdentifier(USER, post.userId().toString());
		return resourceFactory.createRelationshipToOne(identifier);
	}
}
