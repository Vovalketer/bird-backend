package com.gray.bird.post.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.UUID;

import com.gray.bird.post.event.model.PostCreatedEvent;

@SpringJUnitConfig
public class PostEventPublisherTest {
	@Mock
	private ApplicationEventPublisher publisher;
	@InjectMocks
	private PostEventPublisher postEventPublisher;

	@Test
	void testPublishPostCreatedEvent() {
		UUID userId = UUID.randomUUID();
		Long postId = 1L;
		PostCreatedEvent event = new PostCreatedEvent(userId, postId);

		postEventPublisher.publishPostCreatedEvent(userId, postId);

		Mockito.verify(publisher).publishEvent(event);
	}
}
