package com.gray.bird.post.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.gray.bird.post.ReplyAudience;

public record PostProjection(Long id, UUID userId, String text, boolean deleted, boolean hasMedia,
	ReplyAudience replyAudience, Long parentPostId, LocalDateTime createdAt) {
}
