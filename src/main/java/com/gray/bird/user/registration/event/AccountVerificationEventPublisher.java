package com.gray.bird.user.registration.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountVerificationEventPublisher {
	private final ApplicationEventPublisher publisher;

	public void accountVerified(UUID userId) {
		publisher.publishEvent(new AccountVerifiedEvent(userId));
	}
}
