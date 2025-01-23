package com.gray.bird.timeline;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

import com.gray.bird.timeline.dto.TimelineEntryDto;

interface TimelineRepository extends JpaRepository<TimelineEntity, Long> {
	Page<TimelineEntryDto> findByIdUserId(@Param("userId") UUID userId, Pageable pageable);
}
