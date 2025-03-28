package com.gray.bird.user.follow;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import com.gray.bird.user.UserService;
import com.gray.bird.user.follow.dto.FollowCounts;

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

	public void followUser(UUID currentUser, UUID userToFollow) {
		FollowEntity followEntity = new FollowEntity(currentUser, userToFollow);
		repo.save(followEntity);
	}

	public void unfollowUser(UUID currentUser, String usernameToUnfollow) {
		UUID userToUnfollow = userService.getUserIdByUsername(usernameToUnfollow);
		FollowEntity followEntity = new FollowEntity(currentUser, userToUnfollow);
		repo.delete(followEntity);
	}

	public List<UUID> getFollowing(UUID user) {
		return repo.findFollowing(user);
	}

	public List<UUID> getFollowing(String username) {
		UUID userId = userService.getUserIdByUsername(username);
		return repo.findFollowing(userId);
	}

	public List<UUID> getFollowers(UUID user) {
		return repo.findFollowed(user);
	}

	public List<UUID> getFollowers(String username) {
		UUID userId = userService.getUserIdByUsername(username);
		return repo.findFollowing(userId);
	}

	public FollowCounts getFollowCounts(UUID userId) {
		List<FollowEntity> following = repo.findByFollowingUserId(userId);
		List<FollowEntity> followers = repo.findByFollowedUserId(userId);
		return new FollowCounts(userId, following.size(), followers.size());
	}
}
