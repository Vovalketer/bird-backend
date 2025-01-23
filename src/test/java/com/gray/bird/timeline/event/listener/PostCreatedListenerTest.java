package com.gray.bird.timeline.event.listener;

import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.UUID;

import com.gray.bird.post.event.model.PostCreatedEvent;
import com.gray.bird.timeline.TimelineService;

@SpringJUnitConfig
public class PostCreatedListenerTest {
	@Mock
	private TimelineService timelineService;
	@InjectMocks
	private PostCreatedListener listener;

	@Test
	void testOnPostCreated() {
		UUID userId = UUID.randomUUID();
		Long postId = 1L;
		PostCreatedEvent event = new PostCreatedEvent(userId, postId);

		listener.onPostCreated(event);

		Mockito.verify(timelineService).addEntry(userId, postId);
	}
}
