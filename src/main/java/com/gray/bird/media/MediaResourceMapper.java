package com.gray.bird.media;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.gray.bird.common.json.ResourceMapper;
import com.gray.bird.media.dto.MediaAttributes;
import com.gray.bird.media.dto.MediaDto;
import com.gray.bird.media.dto.MediaRelationships;
import com.gray.bird.media.dto.MediaResource;

@Component
@RequiredArgsConstructor
public class MediaResourceMapper implements ResourceMapper<MediaDto, MediaResource> {
	private final MediaUrlBuilder urlBuilder;

	@Override
	public MediaResource toResource(MediaDto data) {
		MediaAttributes mediaAttributes = getMediaAttributes(data);
		MediaRelationships relationships = new MediaRelationships(data.userId(), data.postId());
		MediaResource resource = new MediaResource(data.id(), mediaAttributes, relationships);

		return resource;
	}

	private MediaAttributes getMediaAttributes(MediaDto data) {
		return MediaAttributes.builder()
			.url(urlBuilder.buildMediaUrl(data.filename()))
			.originalFilename(data.originalFilename())
			.sortOrder(data.sortOrder())
			.alt(data.alt())
			.width(data.width())
			.height(data.height())
			.fileSize(data.fileSize())
			.duration(data.duration())
			.mimeType(data.mimeType())
			.build();
	}
}
