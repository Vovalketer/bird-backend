package com.gray.bird.post.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gray.bird.media.dto.MediaDto;
import com.gray.bird.post.ReplyAudience;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
@Builder
public class PostDto {
	private Long id;
	private UUID userId;
	private String text;
	private ReplyAudience replyAudience;
	private boolean deleted;
	private List<MediaDto> media;
	private Long parentPostId;
	private InteractionsDto interactions;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
