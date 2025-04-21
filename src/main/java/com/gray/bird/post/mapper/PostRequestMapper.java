package com.gray.bird.post.mapper;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import com.gray.bird.media.dto.request.MediaMetadataRequest;
import com.gray.bird.media.dto.request.MediaRequest;
import com.gray.bird.post.dto.request.PostContentRequest;
import com.gray.bird.post.dto.request.PostRequest;

@Component
public class PostRequestMapper {
	public PostRequest toPostCreationRequest(
		PostContentRequest content, List<MultipartFile> files, Map<Integer, MediaMetadataRequest> metadata) {
		return new PostRequest(content, new MediaRequest(files, metadata));
	}

	public PostRequest toPostCreationRequest(PostContentRequest content, List<MultipartFile> files) {
		return new PostRequest(content, new MediaRequest(files));
	}

	public PostRequest toPostCreationRequest(PostContentRequest content) {
		return new PostRequest(content, new MediaRequest());
	}
}
