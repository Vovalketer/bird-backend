package com.gray.bird.auth.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthEventPublisher {
	private final ApplicationEventPublisher publisher;

	public void publishUserLoggedInEvent(UUID userId) {
		// TODO: logging
		publisher.publishEvent(new UserLoggedInEvent(userId, LocalDateTime.now()));
	}
}
