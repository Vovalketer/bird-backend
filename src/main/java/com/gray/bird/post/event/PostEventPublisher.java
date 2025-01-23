package com.gray.bird.post.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import com.gray.bird.post.event.model.PostCreatedEvent;

@Service
@RequiredArgsConstructor
public class PostEventPublisher {
	private final ApplicationEventPublisher publisher;

	public void publishPostCreatedEvent(UUID userId, Long postId) {
		PostCreatedEvent event = new PostCreatedEvent(userId, postId);
		publisher.publishEvent(event);
	}
}
