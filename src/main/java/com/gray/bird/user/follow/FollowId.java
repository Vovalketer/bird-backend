package com.gray.bird.user.follow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
class FollowId implements Serializable {
	private Long followingUser;
	private Long followedUser;
}
