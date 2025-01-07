package com.gray.bird.user.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserEventPublisher {
	private final ApplicationEventPublisher publisher;

	public void publishUserCreatedEvent(Long userId, String referenceId, String handle, String email) {
		UserCreatedEvent userCreatedEvent = new UserCreatedEvent(userId, referenceId, handle, email);
		publisher.publishEvent(userCreatedEvent);
	}
}
