package com.gray.bird.postAggregate;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.gray.bird.common.jsonApi.Relationship;
import com.gray.bird.common.jsonApi.ResourceAttributes;
import com.gray.bird.common.jsonApi.ResourceCollectionAggregate;
import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceIdentifier;
import com.gray.bird.common.jsonApi.ResourceSingleAggregate;
import com.gray.bird.common.utils.ResourceFactory;
import com.gray.bird.media.dto.MediaAttributes;
import com.gray.bird.media.dto.MediaProjection;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.postAggregate.dto.PostAttributes;
import com.gray.bird.postAggregate.dto.PostInteractions;

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
		Relationship authorRelationship = resourceFactory.createRelationship(
			resourceFactory.createIdentifier(USER_TYPE, post.userReferenceId()));
		content.addRelationship(USER_TYPE, authorRelationship);
		if (post.parentPostId() != null) {
			ResourceIdentifier parentId =
				resourceFactory.createIdentifier(POST_TYPE, post.parentPostId().toString());
			Relationship rel = resourceFactory.createRelationship(parentId);
			content.addRelationship("parent", rel);
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

	public ResourceSingleAggregate toAggregate(PostAggregate post) {
		ResourceData resourceContent = toResourceContent(post.post());
		ResourceSingleAggregate aggregate = resourceFactory.createSingleAggregate(resourceContent);

		for (var m : post.media()) {
			ResourceIdentifier id = resourceFactory.createIdentifier(MEDIA_TYPE, m.postId().toString());
			aggregate.getData().addRelationship(MEDIA_TYPE, resourceFactory.createRelationship(id));
			ResourceData mediaResourceContent = toResourceContent(m);
			aggregate.includeResource(mediaResourceContent);
		}

		if (post.interactions().isPresent()) {
			PostInteractions postInteractions = new PostInteractions(post.interactions().get().repliesCount(),
				post.interactions().get().likesCount(),
				post.interactions().get().repostsCount());
			resourceContent.addMetadata(INTERACTIONS_TYPE, postInteractions);
		}
		return aggregate;
	}

	public ResourceCollectionAggregate toAggregate(List<PostAggregate> posts) {
		ResourceCollectionAggregate aggregate = resourceFactory.createCollectionAggregate();
		for (var p : posts) {
			ResourceData resourceContent = toResourceContent(p.post());
			for (var m : p.media()) {
				ResourceIdentifier id = resourceFactory.createIdentifier(MEDIA_TYPE, m.postId().toString());
				resourceContent.addRelationship(MEDIA_TYPE, resourceFactory.createRelationship(id));
				ResourceData mediaResourceContent = toResourceContent(m);
				aggregate.includeResource(mediaResourceContent);
			}
			if (p.interactions().isPresent()) {
				PostInteractions postInteractions =
					new PostInteractions(p.interactions().get().repliesCount(),
						p.interactions().get().likesCount(),
						p.interactions().get().repostsCount());
				resourceContent.addMetadata(INTERACTIONS_TYPE, postInteractions);
			}
			aggregate.addData(resourceContent);
		}
		return aggregate;
	}
}
