package com.gray.bird.postAggregator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import com.gray.bird.like.LikeService;
import com.gray.bird.postAggregator.dto.PostEngagement;
import com.gray.bird.repost.RepostService;
import com.gray.bird.testConfig.TestcontainersConfig;

@SpringBootTest
@Testcontainers
@Import(TestcontainersConfig.class)
@Sql(scripts = "/sql/mockaroo/roles.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostInteractionsServiceIT {
	@Autowired
	private PostInteractionsService postInteractionsService;
	@Autowired
	private LikeService likeService;
	@Autowired
	private RepostService repostService;

	private UUID userId;
	private Long testPostId;

	@BeforeAll
	void setUp() {
		userId = UUID.randomUUID();
		testPostId = 2500L;
		likeService.likePost(testPostId, userId);
		likeService.likePost(testPostId, UUID.randomUUID());
		likeService.likePost(testPostId, UUID.randomUUID());

		repostService.repost(testPostId, userId);
		repostService.repost(testPostId, UUID.randomUUID());
		repostService.repost(testPostId, UUID.randomUUID());
	}

	@Test
	void shouldReturnInteractionsWithLikedAndReposted() {
		PostEngagement engagement = postInteractionsService.getInteractionsById(testPostId, userId);
		Assertions.assertThat(engagement.metrics().likesCount()).isEqualTo(3);
		Assertions.assertThat(engagement.metrics().repostsCount()).isEqualTo(3);
		Assertions.assertThat(engagement.userInteractions().isLiked()).isTrue();
		Assertions.assertThat(engagement.userInteractions().isReposted()).isTrue();
	}

	@Test
	void shouldReturnInteractionsWithNullUserInteractions() {
		UUID nullUserId = null;
		PostEngagement engagement = postInteractionsService.getInteractionsById(testPostId, nullUserId);
		Assertions.assertThat(engagement.userInteractions()).isNull();
	}
}
