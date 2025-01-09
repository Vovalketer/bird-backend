package com.gray.bird.auth.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserLoggedInEvent(UUID userId, LocalDateTime time) {
}
