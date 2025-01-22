package com.gray.bird.user.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

@SpringJUnitConfig
public class UserEventPublisherTest {
	@Mock
	private ApplicationEventPublisher publisher;
	@InjectMocks
	private UserEventPublisher userEventPublisher;

	@Test
	void testPublishUserCreatedEvent() {
		String handle = "handle";
		String email = "email@test.com";
		String verificationToken = "token";

		Mockito.doNothing().when(publisher).publishEvent(Mockito.any(UserCreatedEvent.class));

		userEventPublisher.publishUserCreatedEvent(handle, email, verificationToken);

		Mockito.verify(publisher).publishEvent(Mockito.any(UserCreatedEvent.class));
	}
}
