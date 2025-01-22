package com.gray.bird.user.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserEventPublisher {
	private final ApplicationEventPublisher publisher;

	public void publishUserCreatedEvent(String handle, String email, String verificationToken) {
		UserCreatedEvent userCreatedEvent = new UserCreatedEvent(handle, email, verificationToken);
		publisher.publishEvent(userCreatedEvent);
	}
}
