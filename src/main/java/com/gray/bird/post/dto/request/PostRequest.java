package com.gray.bird.post.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.gray.bird.media.dto.request.MediaRequest;

public record PostRequest(PostContentRequest content, MediaRequest media) {
	public PostRequest(@NotNull @Valid PostContentRequest content, MediaRequest media) {
		this.content = content;
		this.media = media;
	}

	public PostRequest(@NotNull @Valid PostContentRequest content) {
		this(content, new MediaRequest());
	}
}
