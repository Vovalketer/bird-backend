package com.gray.bird.user.follow.dto;

import java.util.UUID;

public record FollowCounts(UUID userId, int following, int followers) {
}
