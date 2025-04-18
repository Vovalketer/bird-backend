package com.gray.bird.user.follow;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.gray.bird.user.UserService;
import com.gray.bird.user.follow.dto.FollowCounts;
import com.gray.bird.user.follow.dto.FollowSummary;

@ExtendWith(SpringExtension.class)
public class FollowServiceTest {
	@Mock
	private FollowRepository repo;
	@Mock
	private UserService userService;
	@InjectMocks
	private FollowService followService;

	@Test
	void testFollowUserByUsername() {
		UUID currentUser = UUID.randomUUID();
		UUID userToFollow = UUID.randomUUID();
		String usernameToFollow = "testUsername";
		Mockito.when(userService.getUserIdByUsername(usernameToFollow)).thenReturn(userToFollow);

		followService.followUser(currentUser, usernameToFollow);
		Mockito.verify(repo).save(Mockito.any(FollowEntity.class));
	}

	@Test
	void testFollowUserByUuid() {
		UUID currentUser = UUID.randomUUID();
		UUID userToFollow = UUID.randomUUID();

		followService.followUser(currentUser, userToFollow);
		Mockito.verify(repo).save(Mockito.any(FollowEntity.class));
	}

	@Test
	void testGetFollowersByUuid() {
		UUID userId = UUID.randomUUID();
		List<UUID> followers = List.of(UUID.randomUUID(), UUID.randomUUID());
		Mockito.when(repo.findFollowed(userId)).thenReturn(followers);

		List<UUID> followersRes = followService.getFollowers(userId);
		Mockito.verify(repo).findFollowed(userId);
		Assertions.assertThat(followersRes).containsExactlyElementsOf(followers);
	}

	@Test
	void testGetFollowersByUsername() {
		String username = "testUsername";
		UUID userId = UUID.randomUUID();
		Mockito.when(userService.getUserIdByUsername(username)).thenReturn(userId);
		List<UUID> followers = List.of(UUID.randomUUID(), UUID.randomUUID());
		Mockito.when(repo.findFollowing(userId)).thenReturn(followers);

		List<UUID> followersRes = followService.getFollowers(username);
		Mockito.verify(userService).getUserIdByUsername(username);
		Mockito.verify(repo).findFollowing(userId);
		Assertions.assertThat(followersRes).containsExactlyElementsOf(followers);
	}

	@Test
	void testGetFollowingByUUID() {
		UUID userId = UUID.randomUUID();
		List<UUID> following = List.of(UUID.randomUUID(), UUID.randomUUID());
		Mockito.when(repo.findFollowing(Mockito.any(UUID.class))).thenReturn(following);

		List<UUID> followingRes = followService.getFollowing(userId);
		Assertions.assertThat(followingRes).containsExactlyElementsOf(following);
		Mockito.verify(repo).findFollowing(userId);
	}

	@Test
	void testGetFollowingByUsername() {
		String username = "testUsername";
		UUID userId = UUID.randomUUID();
		List<UUID> following = List.of(UUID.randomUUID(), UUID.randomUUID());
		Mockito.when(repo.findFollowing(userId)).thenReturn(following);
		Mockito.when(userService.getUserIdByUsername(username)).thenReturn(userId);

		List<UUID> followingRes = followService.getFollowing(username);
		Assertions.assertThat(followingRes).containsExactlyElementsOf(following);
		Mockito.verify(repo).findFollowing(userId);
		Mockito.verify(userService).getUserIdByUsername(username);
	}

	@Test
	void testUnfollowUserByUsername() {
		UUID currentUser = UUID.randomUUID();
		String usernameToUnfollow = "testUsername";
		UUID userToUnfollow = UUID.randomUUID();
		Mockito.when(userService.getUserIdByUsername(usernameToUnfollow)).thenReturn(userToUnfollow);

		followService.unfollowUser(currentUser, usernameToUnfollow);
		Mockito.verify(repo).delete(Mockito.any(FollowEntity.class));
	}

	@Test
	void testGetFollowingCountByUserId() {
		UUID userId = UUID.randomUUID();
		List<FollowEntity> follows =
			List.of(new FollowEntity(userId, UUID.randomUUID()), new FollowEntity(userId, UUID.randomUUID()));
		Mockito.when(repo.findByFollowingUserId(userId)).thenReturn(follows);

		FollowCounts followCounts = followService.getFollowCounts(userId);
		Assertions.assertThat(followCounts.userId()).isEqualTo(userId);
		Assertions.assertThat(followCounts.following()).isEqualTo(2);
		Assertions.assertThat(followCounts.followers()).isEqualTo(0);
		Mockito.verify(repo).findByFollowingUserId(userId);
	}

	@Test
	void testGetFollowerCountByUserId() {
		UUID userId = UUID.randomUUID();
		List<FollowEntity> follows =
			List.of(new FollowEntity(userId, UUID.randomUUID()), new FollowEntity(userId, UUID.randomUUID()));
		Mockito.when(repo.findByFollowedUserId(userId)).thenReturn(follows);

		FollowCounts followCounts = followService.getFollowCounts(userId);
		Assertions.assertThat(followCounts.userId()).isEqualTo(userId);
		Assertions.assertThat(followCounts.following()).isEqualTo(0);
		Assertions.assertThat(followCounts.followers()).isEqualTo(2);
		Mockito.verify(repo).findByFollowedUserId(userId);
	}

	@Test
	void isFollowingAndIsFollowedByAreFalseWhenUserIsUnrelated() {
		UUID authUserId = UUID.randomUUID();
		UUID someUser = UUID.randomUUID();
		UUID someOtherUser = UUID.randomUUID();
		List<FollowEntity> follows =
			List.of(new FollowEntity(someUser, UUID.randomUUID()), new FollowEntity(someUser, someOtherUser));
		Mockito.when(repo.findAllByUserId(someUser)).thenReturn(follows);

		FollowSummary followSummary = followService.getFollowSummary(someUser, authUserId);
		Mockito.verify(repo).findAllByUserId(someUser);
		Assertions.assertThat(followSummary.userId()).isEqualTo(someUser);
		Assertions.assertThat(followSummary.userInteractions().isFollowing()).isFalse();
		Assertions.assertThat(followSummary.userInteractions().followedAt()).isNull();
		Assertions.assertThat(followSummary.userInteractions().isFollowedBy()).isFalse();
		Assertions.assertThat(followSummary.userInteractions().followedByAt()).isNull();
	}

	@Test
	void isFollowingIsTrueWhenUserIsFollowing() {
		UUID authUserId = UUID.randomUUID();
		UUID followedUser = UUID.randomUUID();
		List<FollowEntity> follows = List.of(new FollowEntity(authUserId, followedUser, LocalDateTime.now()),
			new FollowEntity(followedUser, UUID.randomUUID(), LocalDateTime.now()));
		Mockito.when(repo.findAllByUserId(followedUser)).thenReturn(follows);

		FollowSummary followSummary = followService.getFollowSummary(followedUser, authUserId);
		Mockito.verify(repo).findAllByUserId(followedUser);
		Assertions.assertThat(followSummary.userId()).isEqualTo(followedUser);
		Assertions.assertThat(followSummary.userInteractions().isFollowing()).isTrue();
		Assertions.assertThat(followSummary.userInteractions().followedAt()).isNotNull();
		Assertions.assertThat(followSummary.userInteractions().isFollowedBy()).isFalse();
		Assertions.assertThat(followSummary.userInteractions().followedByAt()).isNull();
	}

	@Test
	void isFollowedByIsTrueWhenUserIsFollowedBy() {
		UUID authUserId = UUID.randomUUID();
		UUID followingUser = UUID.randomUUID();
		List<FollowEntity> follows = List.of(new FollowEntity(followingUser, authUserId, LocalDateTime.now()),
			new FollowEntity(UUID.randomUUID(), followingUser, LocalDateTime.now()));
		Mockito.when(repo.findAllByUserId(followingUser)).thenReturn(follows);

		FollowSummary followSummary = followService.getFollowSummary(followingUser, authUserId);
		Mockito.verify(repo).findAllByUserId(followingUser);
		Assertions.assertThat(followSummary.userId()).isEqualTo(followingUser);
		Assertions.assertThat(followSummary.userInteractions().isFollowing()).isFalse();
		Assertions.assertThat(followSummary.userInteractions().followedAt()).isNull();
		Assertions.assertThat(followSummary.userInteractions().isFollowedBy()).isTrue();
		Assertions.assertThat(followSummary.userInteractions().followedByAt()).isNotNull();
	}

	@Test
	void isFollowingAndIsFollowedByAreTrueWhenUserIsFollowingAndFollowedBy() {
		UUID authUserId = UUID.randomUUID();
		UUID followingUser = UUID.randomUUID();
		List<FollowEntity> follows = List.of(new FollowEntity(followingUser, authUserId, LocalDateTime.now()),
			new FollowEntity(authUserId, followingUser, LocalDateTime.now()));
		Mockito.when(repo.findAllByUserId(followingUser)).thenReturn(follows);

		FollowSummary followSummary = followService.getFollowSummary(followingUser, authUserId);
		Mockito.verify(repo).findAllByUserId(followingUser);
		Assertions.assertThat(followSummary.userId()).isEqualTo(followingUser);
		Assertions.assertThat(followSummary.userInteractions().isFollowing()).isTrue();
		Assertions.assertThat(followSummary.userInteractions().followedAt()).isNotNull();
		Assertions.assertThat(followSummary.userInteractions().isFollowedBy()).isTrue();
		Assertions.assertThat(followSummary.userInteractions().followedByAt()).isNotNull();

		// commutative test

		Mockito.when(repo.findAllByUserId(authUserId)).thenReturn(follows);
		FollowSummary followSummary2 = followService.getFollowSummary(authUserId, followingUser);
		Mockito.verify(repo).findAllByUserId(authUserId);
		Assertions.assertThat(followSummary2.userId()).isEqualTo(authUserId);
		Assertions.assertThat(followSummary2.userInteractions().isFollowing()).isTrue();
		Assertions.assertThat(followSummary2.userInteractions().followedAt()).isNotNull();
		Assertions.assertThat(followSummary2.userInteractions().isFollowedBy()).isTrue();
		Assertions.assertThat(followSummary2.userInteractions().followedByAt()).isNotNull();
	}

	@Test
	void userInteractionsAreNullWhenUserIsntAuthenticated() {
		UUID unauthenticatedUser = null;
		UUID someUser = UUID.randomUUID();
		UUID someOtherUser = UUID.randomUUID();
		List<FollowEntity> follows = List.of(new FollowEntity(someUser, someOtherUser, LocalDateTime.now()),
			new FollowEntity(someOtherUser, someUser, LocalDateTime.now()));
		Mockito.when(repo.findAllByUserId(someUser)).thenReturn(follows);

		FollowSummary followSummary = followService.getFollowSummary(someUser, unauthenticatedUser);
		Mockito.verify(repo).findAllByUserId(someUser);
		Assertions.assertThat(followSummary.userId()).isEqualTo(someUser);
		Assertions.assertThat(followSummary.userInteractions()).isNull();
	}
}
