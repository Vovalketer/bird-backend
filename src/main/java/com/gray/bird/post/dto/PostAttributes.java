package com.gray.bird.post.dto;

import java.time.LocalDateTime;

import com.gray.bird.post.ReplyType;

public record PostAttributes(String text, ReplyType replyType, LocalDateTime createdAt) {
}
