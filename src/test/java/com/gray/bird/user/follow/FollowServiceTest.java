package com.gray.bird.user.follow;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import com.gray.bird.user.UserService;

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
	void testIsFollowing() {
		UUID currentUser = UUID.randomUUID();
		String usernameToCheck = "testUsername";
		UUID userToCheck = UUID.randomUUID();
		Mockito.when(userService.getUserIdByUsername(usernameToCheck)).thenReturn(userToCheck);
		Mockito.when(repo.existsByFollowingAndFollowedId(currentUser, userToCheck)).thenReturn(true);

		boolean following = followService.isFollowing(currentUser, usernameToCheck);
		Assertions.assertThat(following).isTrue();
		Mockito.verify(userService).getUserIdByUsername(usernameToCheck);
		Mockito.verify(repo).existsByFollowingAndFollowedId(currentUser, userToCheck);
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
}
