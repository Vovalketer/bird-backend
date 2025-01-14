package com.gray.bird.postAggregator;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.gray.bird.common.jsonApi.RelationshipToMany;
import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceDataMapper;
import com.gray.bird.common.jsonApi.ResourceMetadata;
import com.gray.bird.media.MediaResourceMapper;
import com.gray.bird.post.PostResourceMapper;

@Component
@RequiredArgsConstructor
public class PostAggregateResourceMapper implements ResourceDataMapper<PostAggregate> {
	private static final String INTERACTIONS = "interactions";
	private static final String MEDIA = "media";
	private final PostResourceMapper postResourceMapper;
	private final MediaResourceMapper mediaResourceMapper;
	private final PostInteractionsMetadataMapper postInteractionsMetadataMapper;

	@Override
	public ResourceData toResource(PostAggregate data) {
		ResourceData resource = postResourceMapper.toResource(data.post());
		if (data.post().hasMedia() && data.media().size() > 0) {
			RelationshipToMany mediaRelationship = mediaResourceMapper.createMediaRelationship(data.media());
			resource.addRelationshipToMany(MEDIA, mediaRelationship);
		}
		if (data.interactions().isPresent()) {
			ResourceMetadata interactions =
				postInteractionsMetadataMapper.toResourceMetadata(data.interactions().get());
			resource.addMetadata(INTERACTIONS, interactions);
		}

		return resource;
	}
}
