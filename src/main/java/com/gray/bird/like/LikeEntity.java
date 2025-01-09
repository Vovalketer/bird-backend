package com.gray.bird.like;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Table(name = "likes")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class LikeEntity {
	@EmbeddedId
	private LikeId id;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdDate;

	public LikeEntity(UUID userId, Long postId) {
		this.id = new LikeId(userId, postId);
	}

	public LikeEntity() {
		this.id = new LikeId();
	}
}
