package com.gray.bird.media.dto;

public record MediaProjection(Long id, Long postId, String url, String description, int width,
	int height, long fileSize, long duration, String format) {
}
