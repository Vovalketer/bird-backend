package com.gray.bird.media;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gray.bird.media.dto.request.MediaContentRequest;
import com.gray.bird.media.dto.request.MediaInputMetadataRequest;
import com.gray.bird.media.dto.request.MediaRequest;

@Component
public class MediaRequestMapper {
	public MediaRequest toMediaRequest(
		List<MultipartFile> files, Map<Integer, MediaInputMetadataRequest> metadata) {
		List<MediaContentRequest> content = new ArrayList<>();
		int listSize = files != null ? files.size() : 0;
		for (int i = 0; i < listSize; i++) {
			MultipartFile f = files.get(i);
			MediaInputMetadataRequest m = null;
			if (metadata != null) {
				metadata.get(i);
			}
			content.add(new MediaContentRequest(i, f, m));
		}
		return new MediaRequest(content);
	}

	public MediaRequest toMediaRequest(List<MultipartFile> files) {
		return toMediaRequest(files, Map.of());
	}
}
