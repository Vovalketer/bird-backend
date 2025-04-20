package com.gray.bird.media.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public record MediaRequest(List<MultipartFile> files, Map<Integer, MediaMetadataRequest> metadata) {
	public MediaRequest(List<MultipartFile> files, Map<Integer, MediaMetadataRequest> metadata) {
		if (files == null) {
			files = List.of();
		}
		if (metadata == null) {
			metadata = Collections.emptyMap();
		}
		if ((metadata.size() > files.size())) {
			throw new IllegalArgumentException("Metadata size does not match files size");
		}

		// this is to appease the IDE, the constructor shouldnt need this
		this.files = files;
		this.metadata = metadata;
	}
	public MediaRequest(List<MultipartFile> files) {
		this(files, Collections.emptyMap());
	}

	public MediaRequest() {
		this(List.of(), Collections.emptyMap());
	}
}
