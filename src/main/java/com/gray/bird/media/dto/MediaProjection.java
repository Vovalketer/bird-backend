package com.gray.bird.media.dto;

import org.springframework.http.MediaType;

public record MediaProjection(Long id, Long postId, String url, String alt, int width, int height, long size,
	long duration, MediaType type) {
}
