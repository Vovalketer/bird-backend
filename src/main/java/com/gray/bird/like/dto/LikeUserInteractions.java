package com.gray.bird.like.dto;

import java.time.LocalDateTime;

public record LikeUserInteractions(Boolean isLiked, LocalDateTime likedAt) {
}
