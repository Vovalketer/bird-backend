package com.gray.bird.auth.event;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthEventPublisher {
	private final ApplicationEventPublisher publisher;

	public void handleLogin(String username) {
		// TODO: logging
		publisher.publishEvent(new AuthEvent(this, username, EventType.LOGIN, LocalDateTime.now()));
	}
}
