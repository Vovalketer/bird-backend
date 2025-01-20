package com.gray.bird.user.follow;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import com.gray.bird.user.UserService;

@Service
@RequiredArgsConstructor
public class FollowService {
	private final FollowRepository repo;
	private final UserService userService;

	public void followUser(UUID currentUser, String usernameToFollow) {
		UUID userToFollow = userService.getUserIdByUsername(usernameToFollow);
		FollowEntity followEntity = new FollowEntity(currentUser, userToFollow);
		repo.save(followEntity);
	}

	public void unfollowUser(UUID currentUser, String usernameToUnfollow) {
		UUID userToUnfollow = userService.getUserIdByUsername(usernameToUnfollow);
		FollowEntity followEntity = new FollowEntity(currentUser, userToUnfollow);
		repo.delete(followEntity);
	}
}
