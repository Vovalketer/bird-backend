package com.gray.bird.timeline;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import com.gray.bird.timeline.dto.TimelineEntryDto;
import com.gray.bird.user.follow.FollowService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimelineService {
	private final TimelineRepository repository;
	private final FollowService followService;

	public Page<TimelineEntryDto> getHomeTimeline(UUID userId, Pageable pageable) {
		return repository.findByUserId(userId, pageable);
	}

	@Transactional
	public void addEntry(UUID userId, Long postId) {
		repository.save(new TimelineEntity(userId, postId));
	}

	@Transactional
	public void deleteEntry(UUID userId, Long postId) {
		repository.delete(new TimelineEntity(userId, postId));
	}

	public Page<TimelineEntryDto> getFollowingTimeline(UUID userId, Pageable pageable) {
		List<UUID> following = followService.getFollowing(userId);
		return repository.findByUserIdsIn(following, pageable);
	}
}
