package com.gray.bird.media.dto;

import lombok.Builder;

import java.util.Optional;

import com.gray.bird.media.dto.request.MediaInputMetadataRequest;

@Builder
public record MediaMetadata(String originalFilename, int sortOrder,
	Optional<MediaInputMetadataRequest> userProvidedMetadata, ExtractedMetadata extractedMetadata) {
	public MediaMetadata(String originalFilename, int sortOrder, ExtractedMetadata extractedMetadata) {
		this(originalFilename, sortOrder, Optional.empty(), extractedMetadata);
	}
}
