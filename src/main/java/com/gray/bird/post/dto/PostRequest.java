package com.gray.bird.post.dto;

import com.gray.bird.media.dto.MediaRequest;
import com.gray.bird.post.ReplyType;

import jakarta.annotation.Nullable;

public record PostRequest(String text, @Nullable MediaRequest media, ReplyType replyType) {
}
