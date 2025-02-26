package com.gray.bird.postAggregator.dto;

import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;

// userInteractions will be null if the user is not logged in
public record PostEngagement(
	@JsonIgnore Long postId, PostMetrics metrics, @Nullable UserPostInteractions userInteractions) {
	public PostEngagement(Long postId, PostMetrics metrics) {
		this(postId, metrics, null);
	}
}
