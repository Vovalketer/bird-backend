package com.gray.bird.user.follow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import com.gray.bird.testConfig.TestcontainersConfig;
import com.gray.bird.user.follow.dto.FollowCounts;

@SpringBootTest
@Testcontainers
@Import(TestcontainersConfig.class)
public class FollowServiceIT {
	@Autowired
	private FollowService followService;
	@Autowired
	private FollowRepository repo;
	UUID currentUser = UUID.randomUUID();

	@BeforeEach
	void setUp() {
		var following1 = new FollowEntity(currentUser, UUID.randomUUID());
		var following2 = new FollowEntity(currentUser, UUID.randomUUID());
		var following3 = new FollowEntity(currentUser, UUID.randomUUID());
		repo.saveAll(List.of(following1, following2, following3));

		var followed1 = new FollowEntity(UUID.randomUUID(), currentUser);
		var followed2 = new FollowEntity(UUID.randomUUID(), currentUser);
		var followed3 = new FollowEntity(UUID.randomUUID(), currentUser);
		repo.saveAll(List.of(followed1, followed2, followed3));
	}

	@Test
	void testGetFollowCounts() {
		FollowCounts followCounts = followService.getFollowCounts(currentUser);
		Assertions.assertThat(followCounts.following()).isEqualTo(3);
		Assertions.assertThat(followCounts.followers()).isEqualTo(3);
	}
}
