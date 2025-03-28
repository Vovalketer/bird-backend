package com.gray.bird.user.follow.dto;

import org.springframework.lang.Nullable;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record FollowSummary(
	@JsonIgnore UUID userId, FollowCounts followCounts, @Nullable FollowUserInteractions userInteractions) {
}
