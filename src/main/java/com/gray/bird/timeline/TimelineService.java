package com.gray.bird.timeline;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import com.gray.bird.timeline.dto.TimelineEntryDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimelineService {
	private final TimelineRepository repository;

	public Page<TimelineEntryDto> getHomeTimeline(UUID userId, Pageable pageable) {
		return repository.findByIdUserId(userId, pageable);
	}

	@Transactional
	public void addEntry(UUID userId, Long postId) {
		repository.save(new TimelineEntity(userId, postId));
	}

	@Transactional
	public void deleteEntry(UUID userId, Long postId) {
		repository.delete(new TimelineEntity(userId, postId));
	}
}
