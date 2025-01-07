package com.gray.bird.user.event;

public record UserCreatedEvent(Long userId, String username, String handle, String email) {
}
