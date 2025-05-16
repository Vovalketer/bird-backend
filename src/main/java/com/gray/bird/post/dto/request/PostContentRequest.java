package com.gray.bird.post.dto.request;

import jakarta.validation.constraints.NotNull;

import com.gray.bird.post.ReplyAudience;

public record PostContentRequest(String text, @NotNull ReplyAudience replyAudience) {
}
