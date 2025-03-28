package com.gray.bird.user.follow.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record FollowCounts(@JsonIgnore UUID userId, long following, long followers) {
}
