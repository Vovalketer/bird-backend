package com.gray.bird.media;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.gray.bird.common.json.ResourceMapper;
import com.gray.bird.media.dto.MediaAttributes;
import com.gray.bird.media.dto.MediaProjection;
import com.gray.bird.media.dto.MediaRelationships;
import com.gray.bird.media.dto.MediaResource;

@Component
@RequiredArgsConstructor
public class MediaResourceMapper implements ResourceMapper<MediaProjection, MediaResource> {
	@Override
	public MediaResource toResource(MediaProjection data) {
		MediaAttributes mediaAttributes = getMediaAttributes(data);
		MediaRelationships relationships = new MediaRelationships(data.postId());
		MediaResource resource = new MediaResource(data.id(), mediaAttributes, relationships);

		return resource;
	}

	private MediaAttributes getMediaAttributes(MediaProjection data) {
		return new MediaAttributes(
			data.url(), data.alt(), data.width(), data.height(), data.size(), data.duration(), data.type());
	}
}
