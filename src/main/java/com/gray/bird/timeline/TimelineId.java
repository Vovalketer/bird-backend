package com.gray.bird.timeline;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
class TimelineId implements Serializable {
	private UUID userId;
	private Long postId;

	TimelineId() { // for JPA internal use only
	}
}
