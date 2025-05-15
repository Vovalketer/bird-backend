package com.gray.bird.media.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MediaRequest(@NotNull List<MediaContentRequest> content) {
	public MediaRequest() {
		this(List.of());
	}
}
