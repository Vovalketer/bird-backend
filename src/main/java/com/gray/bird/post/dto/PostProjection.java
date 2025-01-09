package com.gray.bird.post.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.gray.bird.post.ReplyType;

public record PostProjection(Long id, UUID userId, String text, boolean deleted, ReplyType replyType,
	Long parentPostId, LocalDateTime createdAt) {
}
