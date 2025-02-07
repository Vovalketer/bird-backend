package com.gray.bird.user.follow;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "follows")
@EntityListeners(AuditingEntityListener.class)
class FollowEntity {
	@EmbeddedId
	private FollowId id;

	@CreatedDate
	private LocalDateTime createdAt;

	public FollowEntity(UUID followingUser, UUID followedUser) {
		this.id = new FollowId(followingUser, followedUser);
	}
}
