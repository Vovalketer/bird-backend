package com.gray.bird.timeline;

import org.springframework.data.annotation.CreatedDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Table(name = "timelines")
@Entity
class TimelineEntity {
	@EmbeddedId
	private TimelineId id;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	TimelineEntity() { // for JPA internal use only
	}

	public TimelineEntity(UUID userId, Long postId) {
		this.id = new TimelineId(userId, postId);
	}
}
