package com.gray.bird.user.follow;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.gray.bird.user.UserEntity;

@Service
@RequiredArgsConstructor
public class FollowService {
	private final FollowRepository repo;

	public void followUser(UserEntity currentUser, UserEntity userToFOllow) {
		FollowEntity followEntity = new FollowEntity(currentUser, userToFOllow);
		repo.save(followEntity);
	}
}
