package com.gray.bird.post.dto;

import jakarta.annotation.Nullable;

import com.gray.bird.media.dto.MediaRequest;
import com.gray.bird.post.ReplyType;

public record PostCreationRequest(String text, @Nullable MediaRequest media, ReplyType replyType) {
}
