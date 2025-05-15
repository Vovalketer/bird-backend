package com.gray.bird.media.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public record MediaContentRequest(
	int fileIndex, MultipartFile file, Optional<MediaInputMetadataRequest> metadata) {
	public MediaContentRequest(
		int fileIndex, @NotNull MultipartFile file, Optional<MediaInputMetadataRequest> metadata) {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("File cannot be null or empty");
		}
		this.fileIndex = fileIndex;
		this.file = file;
		this.metadata = metadata;
	}
	public MediaContentRequest(
		int fileIndex, @NotNull MultipartFile file, MediaInputMetadataRequest metadata) {
		this(fileIndex, file, Optional.ofNullable(metadata));
	}

	public MediaContentRequest(int fileIndex, @NotNull MultipartFile file) {
		this(fileIndex, file, Optional.empty());
	}
}
