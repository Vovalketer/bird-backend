package com.gray.bird.post.event.model;

import java.util.UUID;

public record PostCreatedEvent(UUID userid, Long postId) {
}
