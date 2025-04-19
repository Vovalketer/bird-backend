package com.gray.bird.media.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record MediaRequest(List<MultipartFile> files, Map<Integer, MediaMetadataRequest> metadata) {
	public MediaRequest(List<MultipartFile> files) {
		this(files, new HashMap<>());
	}

	public MediaRequest() {
		this(new ArrayList<>());
	}
}
