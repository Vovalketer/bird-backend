package com.gray.bird.postAggregator.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record PostInteractions(@JsonIgnore Long postId, long repliesCount, long likesCount, long repostsCount,
	Boolean isLiked, Boolean isReposted, LocalDateTime likedAt, LocalDateTime repostedAt) {
	public PostInteractions(Long postId, Long repliesCount, Long likesCount, Long repostsCount) {
		this(postId, repliesCount, likesCount, repostsCount, false, false, null, null);
	}
}
