package com.gray.bird.repost;

import org.springframework.data.annotation.CreatedDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "reposts")
public class RepostEntity {
	@EmbeddedId
	private RepostId id;

	@CreatedDate
	private LocalDateTime repostedAt;

	public RepostEntity(UUID userId, Long postId) {
		this.id = new RepostId(userId, postId);
	}

	public RepostEntity() {
		this.id = new RepostId();
	}
}
