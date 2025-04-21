package com.gray.bird.post.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

import com.gray.bird.media.dto.request.MediaMetadataRequest;
import com.gray.bird.media.dto.request.MediaRequest;

public record PostRequest(PostContentRequest content, MediaRequest media) {
	public PostRequest(@NotNull @Valid PostContentRequest content, MediaRequest media) {
		this.content = content;
		this.media = media;
	}
	public PostRequest(
		PostContentRequest content, List<MultipartFile> files, Map<Integer, MediaMetadataRequest> metadata) {
		this(content, new MediaRequest(files, metadata));
	}

	public PostRequest(PostContentRequest content, List<MultipartFile> files) {
		this(content, new MediaRequest(files));
	}

	public PostRequest(PostContentRequest content) {
		this(content, new MediaRequest());
	}
}
