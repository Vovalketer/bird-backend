package com.gray.bird.post.dto;

import lombok.Builder;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gray.bird.post.ReplyAudience;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("posts")
public record PostAttributes(String text, ReplyAudience replyAudience, LocalDateTime createdAt) {
}
