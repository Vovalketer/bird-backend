package com.gray.bird.postAggregator.dto;

import java.time.LocalDateTime;

import com.gray.bird.post.ReplyType;

public record PostAttributes(String text, ReplyType replyType, LocalDateTime createdAt) {
}
