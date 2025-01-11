package com.gray.bird.postAggregator;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import com.gray.bird.common.jsonApi.RelationshipToOne;
import com.gray.bird.common.jsonApi.ResourceAttributes;
import com.gray.bird.common.jsonApi.ResourceCollectionAggregate;
import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceIdentifier;
import com.gray.bird.common.jsonApi.ResourceSingleAggregate;
import com.gray.bird.common.utils.ResourceFactory;
import com.gray.bird.media.dto.MediaAttributes;
import com.gray.bird.media.dto.MediaProjection;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.postAggregator.dto.PostAttributes;

@Component
@RequiredArgsConstructor
public class PostResourceConverter {
	private static final String USER_TYPE = "user";
	private static final String INTERACTIONS_TYPE = "interactions";
	private static final String MEDIA_TYPE = "media";
	private static final String POST_TYPE = "post";
	private final ResourceFactory resourceFactory;

	private ResourceData toResourceContent(PostProjection post) {
		PostAttributes postAttributes = new PostAttributes(post.text(), post.replyType(), post.createdAt());
		ResourceAttributes attributes = resourceFactory.createAttributes(postAttributes);
		ResourceIdentifier resourceId = resourceFactory.createIdentifier(POST_TYPE, post.id().toString());
		ResourceData content = resourceFactory.createContent(resourceId, attributes);
		RelationshipToOne authorRelationship = resourceFactory.createRelationshipToOne(
			resourceFactory.createIdentifier(USER_TYPE, post.userId().toString()));
		content.addRelationshipToOne(USER_TYPE, authorRelationship);
		if (post.parentPostId() != null) {
			ResourceIdentifier parentId =
				resourceFactory.createIdentifier(POST_TYPE, post.parentPostId().toString());
			RelationshipToOne rel = resourceFactory.createRelationshipToOne(parentId);
			content.addRelationshipToOne("parent", rel);
		}
		return content;
	}

	private ResourceData toResourceContent(MediaProjection media) {
		ResourceIdentifier id = resourceFactory.createIdentifier(MEDIA_TYPE, media.id().toString());
		MediaAttributes mediaAttributes = new MediaAttributes(media.url(),
			media.description(),
			media.width(),
			media.height(),
			media.fileSize(),
			media.duration(),
			media.format());
		ResourceAttributes attributes = resourceFactory.createAttributes(mediaAttributes);

		return resourceFactory.createContent(id, attributes);
	}

	private List<ResourceData> toResourceContent(List<MediaProjection> media) {
		return media.stream().map(m -> toResourceContent(m)).collect(Collectors.toList());
	}

	public ResourceSingleAggregate toAggregate(PostAggregate post) {
		ResourceData resourceData = toResourceContent(post.post());
		ResourceSingleAggregate aggregate = resourceFactory.createSingleAggregate(resourceData);
		List<ResourceData> media = toResourceContent(post.media());
		aggregate.includeAllResources(media);

		if (post.interactions().isPresent()) {
			resourceData.addMetadata(INTERACTIONS_TYPE, post.interactions().get());
		}
		return aggregate;
	}

	public ResourceCollectionAggregate toAggregate(List<PostAggregate> posts) {
		ResourceCollectionAggregate aggregate = resourceFactory.createCollectionAggregate();
		for (var p : posts) {
			ResourceData data = toResourceContent(p.post());
			List<ResourceData> media = toResourceContent(p.media());
			data.addRelationshipToMany(
				MEDIA_TYPE, resourceFactory.createRelationshipToMany(resourceFactory.getIdentifiers(media)));
			aggregate.includeAllResources(media);
			if (p.interactions().isPresent()) {
				data.addMetadata(INTERACTIONS_TYPE, p.interactions().get());
			}
			aggregate.addData(data);
		}
		return aggregate;
	}
}
