package com.gray.bird.user.follow;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gray.bird.user.UserService;
import com.gray.bird.user.follow.dto.FollowCounts;
import com.gray.bird.user.follow.dto.FollowSummary;
import com.gray.bird.user.follow.dto.FollowUserInteractions;

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

	public List<FollowCounts> getFollowCountsByUserIds(List<UUID> userIds) {
		List<FollowEntity> follows = repo.findByFollowingUserIdsIn(userIds);
		Map<UUID, List<FollowEntity>> byFollowing =
			follows.stream().collect(Collectors.groupingBy(_follow -> _follow.getId().getFollowedUser()));
		Map<UUID, List<FollowEntity>> byFollowed =
			follows.stream().collect(Collectors.groupingBy(_follow -> _follow.getId().getFollowingUser()));

		return userIds.stream()
			.map(_userId
				-> new FollowCounts(_userId,
					byFollowing.getOrDefault(_userId, List.of()).size(),
					byFollowed.getOrDefault(_userId, List.of()).size()))
			.collect(Collectors.toList());
	}

	public FollowSummary getFollowSummary(UUID userId, UUID authUserId) {
		List<FollowEntity> follows = repo.findAllByUserId(userId);
		return new FollowSummary(
			userId, getFollowCounts(userId, follows), getUserInteractions(userId, authUserId, follows));
	}

	public List<FollowSummary> getFollowSummaryByUserIds(List<UUID> userIds, UUID authUserId) {
		List<FollowEntity> follows = repo.findByFollowingUserIdsIn(userIds);

		return userIds.stream()
			.map(_userId -> {
				return new FollowSummary(_userId,
					getFollowCounts(_userId, follows),
					getUserInteractions(_userId, authUserId, follows));
			})
			.collect(Collectors.toList());
	}

	private FollowCounts getFollowCounts(UUID userId, List<FollowEntity> follows) {
		Map<UUID, Long> followingCount = follows.stream().collect(
			Collectors.groupingBy(_follow -> _follow.getId().getFollowingUser(), Collectors.counting()));
		Map<UUID, Long> followedCount = follows.stream().collect(
			Collectors.groupingBy(_follow -> _follow.getId().getFollowedUser(), Collectors.counting()));
		return new FollowCounts(
			userId, followingCount.getOrDefault(userId, 0L), followedCount.getOrDefault(userId, 0L));
	}

	private FollowUserInteractions getUserInteractions(
		UUID userId, UUID authUserId, List<FollowEntity> follows) {
		if (authUserId == null) {
			return null;
		}
		Optional<FollowEntity> following =
			follows.stream().filter(_follow -> _follow.equals(authUserId, userId)).findAny();
		Optional<FollowEntity> followed =
			follows.stream().filter(_follow -> _follow.equals(userId, authUserId)).findAny();

		return new FollowUserInteractions(following.isPresent(),
			following.map(FollowEntity::getCreatedAt).orElse(null),
			followed.isPresent(),
			followed.map(FollowEntity::getCreatedAt).orElse(null));
	}
}
