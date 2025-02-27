package com.gray.bird.like.dto;

import java.util.Optional;

public record LikeSummary(long postId, long likesCount, Optional<LikeUserInteractions> userInteractions) {
	public LikeSummary(Long postId, long likesCount, LikeUserInteractions userInteractions) {
		this(postId, likesCount, Optional.of(userInteractions));
	}
	public LikeSummary(Long postId, long likesCount) {
		this(postId, likesCount, Optional.ofNullable(null));
	}
}
