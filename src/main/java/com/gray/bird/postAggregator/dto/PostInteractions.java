package com.gray.bird.postAggregator.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record PostInteractions(
	@JsonIgnore Long postId, Long repliesCount, Long likesCount, Long repostsCount) {
}
