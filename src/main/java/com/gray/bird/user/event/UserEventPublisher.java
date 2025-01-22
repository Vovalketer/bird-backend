package com.gray.bird.user.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserEventPublisher {
	private final ApplicationEventPublisher publisher;

	public void publishUserCreatedEvent(
		UUID uuid, String username, String handle, String email, String verificationToken) {
		UserCreatedEvent userCreatedEvent =
			new UserCreatedEvent(uuid, username, handle, email, verificationToken);
		publisher.publishEvent(userCreatedEvent);
	}
}
