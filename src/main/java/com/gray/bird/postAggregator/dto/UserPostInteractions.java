package com.gray.bird.postAggregator.dto;

import java.time.LocalDateTime;

public record UserPostInteractions(
	Boolean isLiked, LocalDateTime likedAt, Boolean isReposted, LocalDateTime repostedAt) {
}
