package com.gray.bird.user.event;

import java.util.UUID;

public record UserCreatedEvent(UUID userId, String username, String handle, String email) {
}
