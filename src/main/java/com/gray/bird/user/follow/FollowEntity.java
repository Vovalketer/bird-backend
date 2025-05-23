package com.gray.bird.user.follow;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
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
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public FollowEntity(UUID followingUser, UUID followedUser) {
		this.id = new FollowId(followingUser, followedUser);
	}

	public FollowEntity(UUID followingUser, UUID followedUser, LocalDateTime createdAt) {
		this.id = new FollowId(followingUser, followedUser);
		this.createdAt = createdAt;
	}

	public boolean equals(UUID followingUser, UUID followedUser) {
		return this.id.getFollowingUser().equals(followingUser)
			&& this.id.getFollowedUser().equals(followedUser);
	}
}
