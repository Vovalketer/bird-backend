package com.gray.bird.repost.dto;

import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

public record RepostSummary(
	Long postId, long repostsCount, Boolean isReposted, @Nullable LocalDateTime createdAt) {
}
