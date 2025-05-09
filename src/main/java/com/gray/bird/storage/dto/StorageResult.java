package com.gray.bird.storage.dto;

import org.springframework.core.io.Resource;

import lombok.Builder;

@Builder
public record StorageResult(String storageFilename, String originalFilename, String extension, long fileSize,
	String relativePath, Resource fileResource) {
}
