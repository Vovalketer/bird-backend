package com.gray.bird.user.follow;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import com.gray.bird.user.UserEntity;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "follows")
@EntityListeners(AuditingEntityListener.class)
public class FollowEntity {
	@EmbeddedId
	private FollowId id = new FollowId();

	@ManyToOne
	@JoinColumn(name = "following_id")
	@MapsId("followingUser")
	private UserEntity followingUser;

	@ManyToOne
	@JoinColumn(name = "followed_id")
	@MapsId("followedUser")
	private UserEntity followedUser;

	@CreatedDate
	private LocalDateTime createdAt;

	public FollowEntity(UserEntity followingUser, UserEntity followedUser) {
		this.followingUser = followingUser;
		this.followedUser = followedUser;
	}
}
