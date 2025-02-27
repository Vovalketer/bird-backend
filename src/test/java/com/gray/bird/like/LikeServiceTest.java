package com.gray.bird.like;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import com.gray.bird.like.dto.LikeSummary;

@ExtendWith(SpringExtension.class)
public class LikeServiceTest {
	@Mock
	private LikeRepository likeRepository;
	@InjectMocks
	private LikeService likeService;

	@Test
	void givenPostIdAndUserIdThenReturnLikeSummaryWithUserInteractions() {
		UUID userId = UUID.randomUUID();
		Long postId = 1L;
		LikeEntity likedByCurrentUser = new LikeEntity(userId, postId);

		// list with only one post liked by the current user
		List<LikeEntity> likes = List.of(likedByCurrentUser, new LikeEntity(UUID.randomUUID(), postId));

		Mockito.when(likeRepository.findByPostId(postId)).thenReturn(likes);

		LikeSummary likeSummary = likeService.getLikeSummary(userId, postId);
		Assertions.assertThat(likeSummary.postId()).isEqualTo(postId);
		Assertions.assertThat(likeSummary.likesCount()).isEqualTo(2);
		Assertions.assertThat(likeSummary.userInteractions()).isPresent();
		Assertions.assertThat(likeSummary.userInteractions().get().isLiked()).isTrue();
	}

	@Test
	void givenPostIdAndNullUserIdThenReturnLikeSummaryWithEmptyUserInteractions() {
		UUID userId = null;
		Long postId = 1L;

		List<LikeEntity> likes =
			List.of(new LikeEntity(UUID.randomUUID(), postId), new LikeEntity(UUID.randomUUID(), postId));

		Mockito.when(likeRepository.findByPostId(postId)).thenReturn(likes);

		LikeSummary likeSummary = likeService.getLikeSummary(userId, postId);

		Assertions.assertThat(likeSummary.postId()).isEqualTo(postId);
		Assertions.assertThat(likeSummary.likesCount()).isEqualTo(2);
		Assertions.assertThat(likeSummary.userInteractions()).isEmpty();
	}

	@Test
	void givenListOfPostIdsAndUserIdThenReturnListOfLikeSummariesWithUserInteractions() {
		UUID userId = UUID.randomUUID();
		List<Long> postIds = List.of(1L, 2L, 3L);

		LikeEntity likedByCurrentUser = new LikeEntity(userId, postIds.get(0));
		// list with only one post liked by the user
		List<LikeEntity> likes = List.of(likedByCurrentUser,
			new LikeEntity(UUID.randomUUID(), postIds.get(1)),
			new LikeEntity(UUID.randomUUID(), postIds.get(2)));

		Mockito.when(likeRepository.findByPostIdsIn(postIds)).thenReturn(likes);

		List<LikeSummary> likeSummaryList = likeService.getLikeSummaryByPostIds(userId, postIds);
		Assertions.assertThat(likeSummaryList).hasSize(postIds.size());
		Assertions.assertThat(likeSummaryList.stream().filter(l -> l.userInteractions().isPresent()).count())
			.isEqualTo(postIds.size());
		Assertions
			.assertThat(likeSummaryList.stream()
					.filter(l -> l.userInteractions().map(u -> u.isLiked()).orElse(false))
					.count())
			.isEqualTo(1);
		Assertions
			.assertThat(likeSummaryList.stream()
					.filter(l -> l.userInteractions().map(u -> !u.isLiked()).orElse(true))
					.count())
			.isEqualTo(2);
	}

	@Test
	void givenListOfPostIdsAndNullUserIdThenReturnListOfLikeSummariesWithEmptyUserInteractions() {
		UUID userId = null;
		List<Long> postIds = List.of(1L, 2L, 3L);

		List<LikeEntity> likes = List.of(new LikeEntity(UUID.randomUUID(), postIds.get(0)),
			new LikeEntity(UUID.randomUUID(), postIds.get(1)),
			new LikeEntity(UUID.randomUUID(), postIds.get(2)));

		Mockito.when(likeRepository.findByPostIdsIn(postIds)).thenReturn(likes);

		List<LikeSummary> likeSummaryList = likeService.getLikeSummaryByPostIds(userId, postIds);

		Assertions.assertThat(likeSummaryList).hasSize(postIds.size());
		Assertions.assertThat(likeSummaryList.stream().filter(l -> l.userInteractions().isPresent()).count())
			.isEqualTo(0);
	}
}
