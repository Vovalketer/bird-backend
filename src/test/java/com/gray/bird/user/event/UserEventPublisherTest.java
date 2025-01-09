package com.gray.bird.user.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.UUID;

@SpringJUnitConfig
public class UserEventPublisherTest {
	@Mock
	private ApplicationEventPublisher publisher;
	@InjectMocks
	private UserEventPublisher userEventPublisher;

	@Test
	void testPublishUserCreatedEvent() {
		UUID uuid = UUID.randomUUID();
		String username = "testUser";
		String handle = "handle";
		String email = "email@test.com";

		Mockito.doNothing().when(publisher).publishEvent(Mockito.any(UserCreatedEvent.class));

		userEventPublisher.publishUserCreatedEvent(uuid, username, handle, email);

		Mockito.verify(publisher).publishEvent(Mockito.any(UserCreatedEvent.class));
	}
}
