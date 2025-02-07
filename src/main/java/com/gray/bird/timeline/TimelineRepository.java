package com.gray.bird.timeline;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

import com.gray.bird.timeline.dto.TimelineEntryDto;

interface TimelineRepository extends JpaRepository<TimelineEntity, TimelineId> {
	@Query("SELECT new com.gray.bird.timeline.dto.TimelineEntryDto(t.id.userId, t.id.postId) FROM "
		+ "TimelineEntity t WHERE t.id.userId = :userId")
	Page<TimelineEntryDto>
	findByUserId(@Param("userId") UUID userId, Pageable pageable);

	@Query("SELECT new com.gray.bird.timeline.dto.TimelineEntryDto(t.id.userId, t.id.postId) FROM "
		+ "TimelineEntity t WHERE t.id.userId IN :userIds")
	Page<TimelineEntryDto>
	findByUserIdsIn(Iterable<UUID> userIds, Pageable pageable);
}
