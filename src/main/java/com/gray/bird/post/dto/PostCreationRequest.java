package com.gray.bird.post.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import com.gray.bird.media.dto.MediaRequest;
import com.gray.bird.post.ReplyType;

public record PostCreationRequest(String text, @Nullable MediaRequest media, @NotNull ReplyType replyType) {
}
