package com.gray.bird.post.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gray.bird.media.view.MediaView;
import com.gray.bird.post.ReplyType;
import com.gray.bird.post.view.InteractionsView;

@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record PostData(Long id, String userReferenceId, String text, ReplyType replyType, boolean deleted,
		List<MediaView> media, Long parentPostId, InteractionsView interactions, LocalDateTime createdAt,
		LocalDateTime updatedAt

) {
}
