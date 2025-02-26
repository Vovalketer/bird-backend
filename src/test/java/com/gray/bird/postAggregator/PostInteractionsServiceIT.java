package com.gray.bird.postAggregator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import com.gray.bird.like.LikeService;
import com.gray.bird.post.PostService;
import com.gray.bird.post.ReplyType;
import com.gray.bird.post.dto.PostCreationRequest;
import com.gray.bird.postAggregator.dto.PostEngagement;
import com.gray.bird.repost.RepostService;
import com.gray.bird.testConfig.TestcontainersConfig;
import com.gray.bird.user.UserService;
import com.gray.bird.user.dto.UserCreationRequest;

@SpringBootTest
@Testcontainers
@Import(TestcontainersConfig.class)
@Sql(scripts = "/sql/mockaroo/roles.sql")
public class PostInteractionsServiceIT {
	@Autowired
	private PostInteractionsService postInteractionsService;
	@Autowired
	private LikeService likeService;
	@Autowired
	private RepostService repostService;
	@Autowired
	private PostService postService;
	@Autowired
	private UserService userService;

	private UUID userId;
	private Long testPostId;

	@BeforeEach
	void setUp() {
		var user = userService.createUser(
			new UserCreationRequest("testUsername", "test@testemail.com", "testPassword", "testHandle"));
		userId = user.uuid();
		var post =
			postService.createPost(new PostCreationRequest("testPost", null, ReplyType.EVERYONE), userId);
		likeService.likePost(userId, post.id());
		testPostId = post.id();

		repostService.repost(userId, post.id());
	}

	@Test
	void shouldReturnInteractionsWithLikedAndReposted() {
		PostEngagement engagement = postInteractionsService.getInteractionsById(testPostId, userId);
		Assertions.assertThat(engagement.metrics().likesCount()).isEqualTo(1);
		Assertions.assertThat(engagement.metrics().repostsCount()).isEqualTo(1);
		Assertions.assertThat(engagement.userInteractions().isLiked()).isTrue();
		Assertions.assertThat(engagement.userInteractions().isReposted()).isTrue();
	}

	@Test
	void shouldReturnInteractionsWithNullUserInteractions() {
		PostEngagement engagement = postInteractionsService.getInteractionsById(testPostId);
		Assertions.assertThat(engagement.userInteractions()).isNull();
	}
}
