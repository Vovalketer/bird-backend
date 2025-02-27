package com.gray.bird.postAggregator;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.gray.bird.like.LikeService;
import com.gray.bird.like.dto.LikeSummary;
import com.gray.bird.like.dto.LikeUserInteractions;
import com.gray.bird.post.PostService;
import com.gray.bird.post.dto.RepliesCount;
import com.gray.bird.postAggregator.dto.PostEngagement;
import com.gray.bird.repost.RepostService;
import com.gray.bird.repost.dto.RepostSummary;
import com.gray.bird.repost.dto.RepostUserInteractions;

@ExtendWith(SpringExtension.class)
public class PostInteractionsServiceTest {
	@Mock
	private LikeService likesService;
	@Mock
	private RepostService repostService;
	@Mock
	private PostService postService;

	@InjectMocks
	private PostInteractionsService postInteractionsService;

	private UUID userId = UUID.randomUUID();
	private UUID nullUserId = null;
	private Long interactedPostId = 1L;
	private Long notInteractedPostId = 2L;
	private List<Long> postIds = List.of(interactedPostId, notInteractedPostId);

	private List<LikeSummary> likeSummaries;
	private List<RepostSummary> repostSummaries;
	private List<RepliesCount> repliesCounts;

	// interacted
	private LikeSummary likedSummary;
	private RepostSummary repostedSummary;
	private RepliesCount interactedRepliesCount;

	// not interacted
	private LikeSummary notLikedSummary;
	private RepostSummary notRepostSummary;
	private RepliesCount notInteractedRepliesCount;

	@BeforeEach
	void setUp() {
		likedSummary =
			new LikeSummary(interactedPostId, 5L, new LikeUserInteractions(true, LocalDateTime.now()));
		notLikedSummary = new LikeSummary(notInteractedPostId, 10L);
		repostedSummary =
			new RepostSummary(interactedPostId, 5L, new RepostUserInteractions(true, LocalDateTime.now()));
		notRepostSummary = new RepostSummary(notInteractedPostId, 10L);
		interactedRepliesCount = new RepliesCount(interactedPostId, 2L);
		notInteractedRepliesCount = new RepliesCount(notInteractedPostId, 4L);

		likeSummaries = List.of(likedSummary, notLikedSummary);
		repostSummaries = List.of(repostedSummary, notRepostSummary);
		repliesCounts = List.of(interactedRepliesCount, notInteractedRepliesCount);
	}

	@Test
	void testGetAllInteractionsByIds() {
		Mockito.when(likesService.getLikeSummaryByPostIds(userId, postIds)).thenReturn(likeSummaries);
		Mockito.when(repostService.getRepostSummaryByPostIds(userId, postIds)).thenReturn(repostSummaries);
		Mockito.when(postService.getRepliesCountByPostIds(postIds)).thenReturn(repliesCounts);
		List<PostEngagement> engagement = postInteractionsService.getAllInteractionsByIds(postIds, userId);

		Assertions.assertThat(engagement).hasSize(postIds.size());
	}

	@Test
	void testGetAllInteractionsByIdsWithUser() {
		Mockito.when(likesService.getLikeSummaryByPostIds(userId, postIds)).thenReturn(likeSummaries);
		Mockito.when(repostService.getRepostSummaryByPostIds(userId, postIds)).thenReturn(repostSummaries);
		Mockito.when(postService.getRepliesCountByPostIds(postIds)).thenReturn(repliesCounts);

		List<PostEngagement> engagement = postInteractionsService.getAllInteractionsByIds(postIds, userId);

		Assertions.assertThat(engagement).hasSize(postIds.size());
	}

	@Test
	void testGetInteractionsById() {
		Mockito.when(likesService.getLikeSummary(nullUserId, notInteractedPostId))
			.thenReturn(notLikedSummary);
		Mockito.when(repostService.getRepostSummary(nullUserId, notInteractedPostId))
			.thenReturn(notRepostSummary);
		Mockito.when(postService.getRepliesCountByPostId(notInteractedPostId))
			.thenReturn(notInteractedRepliesCount);
		PostEngagement interactions =
			postInteractionsService.getInteractionsById(notInteractedPostId, nullUserId);

		Assertions.assertThat(interactions).isNotNull();
		Assertions.assertThat(interactions.postId()).isEqualTo(notInteractedPostId);
		Assertions.assertThat(interactions.userInteractions()).isNull();
		Assertions.assertThat(interactions.metrics().likesCount()).isEqualTo(notLikedSummary.likesCount());
		Assertions.assertThat(interactions.metrics().repostsCount())
			.isEqualTo(notRepostSummary.repostsCount());
		Assertions.assertThat(interactions.metrics().repliesCount())
			.isEqualTo(notInteractedRepliesCount.repliesCount());
	}

	@Test
	void testGetInteractionsByIdWithUser() {
		Mockito.when(likesService.getLikeSummary(userId, interactedPostId)).thenReturn(likedSummary);
		Mockito.when(repostService.getRepostSummary(userId, interactedPostId)).thenReturn(repostedSummary);
		Mockito.when(postService.getRepliesCountByPostId(interactedPostId))
			.thenReturn(interactedRepliesCount);

		PostEngagement interactions = postInteractionsService.getInteractionsById(interactedPostId, userId);

		Assertions.assertThat(interactions).isNotNull();
		Assertions.assertThat(interactions.postId()).isEqualTo(interactedPostId);
		Assertions.assertThat(interactions.userInteractions()).isNotNull();
		Assertions.assertThat(interactions.userInteractions().isLiked()).isEqualTo(true);
		Assertions.assertThat(interactions.userInteractions().isReposted()).isEqualTo(true);
		Assertions.assertThat(interactions.metrics().likesCount()).isEqualTo(likedSummary.likesCount());
		Assertions.assertThat(interactions.metrics().repostsCount())
			.isEqualTo(repostedSummary.repostsCount());
		Assertions.assertThat(interactions.metrics().repliesCount())
			.isEqualTo(interactedRepliesCount.repliesCount());
	}
}
