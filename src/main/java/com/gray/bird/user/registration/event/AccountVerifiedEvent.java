package com.gray.bird.user.registration.event;

import java.util.UUID;

public record AccountVerifiedEvent(UUID userId) {
}
