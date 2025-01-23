package com.gray.bird.timeline.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.gray.bird.post.event.model.PostCreatedEvent;
import com.gray.bird.timeline.TimelineService;

@Component
@RequiredArgsConstructor
public class PostCreatedListener {
	private final TimelineService timelineService;

	@EventListener
	public void onPostCreated(PostCreatedEvent event) {
		timelineService.addEntry(event.userid(), event.postId());
	}
}
