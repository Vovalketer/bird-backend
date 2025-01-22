package com.gray.bird.user.event;

public record UserCreatedEvent(String handle, String email, String token) {
}
