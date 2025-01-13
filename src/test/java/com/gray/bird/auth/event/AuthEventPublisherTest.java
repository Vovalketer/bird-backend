package com.gray.bird.auth.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.UUID;

@SpringJUnitConfig
public class AuthEventPublisherTest {
	@Mock
	private ApplicationEventPublisher publisher;
	@InjectMocks
	AuthEventPublisher authEventPublisher;

	@Test
	void testPublishUserLoggedInEvent() {
		UUID userId = UUID.randomUUID();
		Mockito.doNothing().when(publisher).publishEvent(Mockito.any(UserLoggedInEvent.class));

		authEventPublisher.publishUserLoggedInEvent(userId);

		Mockito.verify(publisher, Mockito.times(1)).publishEvent(Mockito.any(UserLoggedInEvent.class));
	}
}
