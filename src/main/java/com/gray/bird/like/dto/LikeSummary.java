package com.gray.bird.like.dto;

import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

// createdAt will be null if isLiked is false
public record LikeSummary(long postId, long likesCount, Boolean isLiked, @Nullable LocalDateTime createdAt) {
}
