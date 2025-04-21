package com.gray.bird.post.dto.request;

import jakarta.validation.constraints.NotNull;

import com.gray.bird.post.ReplyType;

public record PostContentRequest(String text, @NotNull ReplyType replyType) {
}
