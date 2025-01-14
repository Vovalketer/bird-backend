package com.gray.bird.media;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import com.gray.bird.common.jsonApi.RelationshipToMany;
import com.gray.bird.common.jsonApi.RelationshipToOne;
import com.gray.bird.common.jsonApi.ResourceAttributes;
import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceDataMapper;
import com.gray.bird.common.jsonApi.ResourceFactory;
import com.gray.bird.common.jsonApi.ResourceIdentifier;
import com.gray.bird.media.dto.MediaAttributes;
import com.gray.bird.media.dto.MediaProjection;

@Component
@RequiredArgsConstructor
public class MediaResourceMapper implements ResourceDataMapper<MediaProjection> {
	private static final String POST = "post";
	private static final String MEDIA = "media";
	private final ResourceFactory resourceFactory;

	@Override
	public ResourceData toResource(MediaProjection data) {
		MediaAttributes mediaAttributes = getMediaAttributes(data);
		ResourceAttributes attributes = resourceFactory.createAttributes(mediaAttributes);
		ResourceIdentifier identifier = resourceFactory.createIdentifier(MEDIA, data.id().toString());
		ResourceData resource = resourceFactory.createData(identifier, attributes);
		resource.addRelationshipToOne(POST, createPostRelationship(data));

		return resource;
	}

	private MediaAttributes getMediaAttributes(MediaProjection data) {
		return new MediaAttributes(data.url(),
			data.description(),
			data.width(),
			data.height(),
			data.fileSize(),
			data.duration(),
			data.format());
	}

	private RelationshipToOne createPostRelationship(MediaProjection media) {
		ResourceIdentifier identifier = resourceFactory.createIdentifier(POST, media.postId().toString());
		return resourceFactory.createRelationshipToOne(identifier);
	}

	public RelationshipToMany createMediaRelationship(List<MediaProjection> media) {
		List<ResourceIdentifier> identifiers =
			media.stream()
				.map(m -> resourceFactory.createIdentifier(MEDIA, m.id().toString()))
				.collect(Collectors.toList());
		return resourceFactory.createRelationshipToMany(identifiers);
	}
}
