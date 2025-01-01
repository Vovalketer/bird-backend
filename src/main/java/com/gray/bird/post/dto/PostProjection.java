package com.gray.bird.post.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gray.bird.post.ReplyType;

public record PostProjection(Long id, @JsonIgnore Long userId, String userReferenceId, String text,
	boolean deleted, ReplyType replyType, Long parentPostId, LocalDateTime createdAt) {
}
