package com.gray.bird.media.dto;

import lombok.Builder;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Builder
public record MediaDto(Long id, Long postId, UUID userId, int sortOrder, String relativePath, String filename,
	String originalFilename, String alt, int width, int height, long fileSize, Integer duration,
	String mimeType) {
}
