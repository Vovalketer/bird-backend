package com.gray.bird.media.dto;

import lombok.Builder;

@Builder
public record MediaAttributes(String url, String originalFilename, int sortOrder, String alt, int width,
	int height, long fileSize, Integer duration, String mimeType) {
}
