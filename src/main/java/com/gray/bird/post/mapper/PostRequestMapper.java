package com.gray.bird.post.mapper;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import com.gray.bird.media.MediaRequestMapper;
import com.gray.bird.media.dto.request.MediaInputMetadataRequest;
import com.gray.bird.media.dto.request.MediaRequest;
import com.gray.bird.post.dto.request.PostContentRequest;
import com.gray.bird.post.dto.request.PostRequest;

@Component
@RequiredArgsConstructor
public class PostRequestMapper {
	private final MediaRequestMapper mediaRequestMapper;

	public PostRequest toPostCreationRequest(PostContentRequest content, List<MultipartFile> files,
		Map<Integer, MediaInputMetadataRequest> metadata) {
		MediaRequest mediaRequest = mediaRequestMapper.toMediaRequest(files, metadata);
		return new PostRequest(content, mediaRequest);
	}

	public PostRequest toPostCreationRequest(PostContentRequest content, List<MultipartFile> files) {
		MediaRequest mediaRequest = mediaRequestMapper.toMediaRequest(files);
		return new PostRequest(content, mediaRequest);
	}

	public PostRequest toPostCreationRequest(PostContentRequest content) {
		return new PostRequest(content, new MediaRequest());
	}
}
