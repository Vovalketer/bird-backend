package com.gray.bird.user.follow.dto;

import java.time.LocalDateTime;

public record FollowUserInteractions(
	boolean isFollowing, LocalDateTime followedAt, boolean isFollowedBy, LocalDateTime followedByAt) {
}
