package com.gray.bird.media.dto;

import lombok.Builder;

@Builder
public record ExtractedMetadata(
	String filename, String extension, String mimeType, int width, int height, Integer orientation) {
}
