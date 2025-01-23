package com.gray.bird.timeline.dto;

import java.util.UUID;

public record TimelineEntryDto(UUID userId, Long postId) {
}
