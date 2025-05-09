package com.gray.bird.storage.dto;

import org.springframework.lang.Nullable;

import lombok.Builder;

import java.io.InputStream;

@Builder
public record StorageRequest(InputStream fileStream, String originalFilename, String targetFilename,
	@Nullable String directory, long fileSize) {
}
