package com.gray.bird.repost;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import com.gray.bird.repost.dto.RepostSummary;

@ExtendWith(SpringExtension.class)
public class RepostServiceTest {
	@Mock
	private RepostRepository repostRepository;
	@InjectMocks
	private RepostService repostService;

	@Test
	void givenPostIdAndUserIdThenReturnRepostSummaryWithUserInteractions() {
		UUID userId = UUID.randomUUID();
		Long postId = 1L;
		RepostEntity repostedByCurrentUser = new RepostEntity(userId, postId);

		List<RepostEntity> reposts =
			List.of(repostedByCurrentUser, new RepostEntity(UUID.randomUUID(), postId));

		Mockito.when(repostRepository.findByPostId(postId)).thenReturn(reposts);

		RepostSummary repostSummary = repostService.getRepostSummary(userId, postId);

		Assertions.assertThat(repostSummary.postId()).isEqualTo(postId);
		Assertions.assertThat(repostSummary.repostsCount()).isEqualTo(2);
		Assertions.assertThat(repostSummary.userInteractions()).isPresent();
		Assertions.assertThat(repostSummary.userInteractions().get().isReposted()).isTrue();
	}

	@Test
	void givenPostIdAndNullUserIdThenReturnRepostSummaryWithEmptyUserInteractions() {
		UUID nullUserId = null;
		Long postId = 1L;

		List<RepostEntity> reposts =
			List.of(new RepostEntity(UUID.randomUUID(), postId), new RepostEntity(UUID.randomUUID(), postId));

		Mockito.when(repostRepository.findByPostId(postId)).thenReturn(reposts);

		RepostSummary repostSummary = repostService.getRepostSummary(nullUserId, postId);

		Assertions.assertThat(repostSummary.postId()).isEqualTo(postId);
		Assertions.assertThat(repostSummary.repostsCount()).isEqualTo(2);
		Assertions.assertThat(repostSummary.userInteractions()).isEmpty();
	}

	@Test
	void givenListOfPostIdsAndUserIdThenReturnListOfRepostSummariesWithUserInteractions() {
		UUID userId = UUID.randomUUID();
		List<Long> postIds = List.of(1L, 2L, 3L);

		RepostEntity repostedByCurrentUser = new RepostEntity(userId, postIds.get(0));

		List<RepostEntity> reposts = List.of(repostedByCurrentUser,
			new RepostEntity(UUID.randomUUID(), postIds.get(1)),
			new RepostEntity(UUID.randomUUID(), postIds.get(2)));

		Mockito.when(repostRepository.findByPostIdsIn(postIds)).thenReturn(reposts);

		List<RepostSummary> repostSummaryList = repostService.getRepostSummaryByPostIds(userId, postIds);

		Assertions.assertThat(repostSummaryList).hasSize(postIds.size());
		Assertions
			.assertThat(repostSummaryList.stream().filter(l -> l.userInteractions().isPresent()).count())
			.isEqualTo(postIds.size());
		Assertions
			.assertThat(repostSummaryList.stream()
					.filter(l -> l.userInteractions().map(r -> r.isReposted()).orElse(false))
					.count())
			.isEqualTo(1);
		Assertions
			.assertThat(repostSummaryList.stream()
					.filter(l -> l.userInteractions().map(r -> !r.isReposted()).orElse(true))
					.count())
			.isEqualTo(2);
	}

	@Test
	void givenListOfPostIdsAndNullUserIdThenReturnListOfRepostSummariesWithEmptyUserInteractions() {
		UUID nullUserId = null;
		List<Long> postIds = List.of(1L, 2L, 3L);

		List<RepostEntity> reposts = List.of(new RepostEntity(UUID.randomUUID(), postIds.get(0)),
			new RepostEntity(UUID.randomUUID(), postIds.get(1)),
			new RepostEntity(UUID.randomUUID(), postIds.get(2)));

		Mockito.when(repostRepository.findByPostIdsIn(postIds)).thenReturn(reposts);

		List<RepostSummary> repostSummaryList = repostService.getRepostSummaryByPostIds(nullUserId, postIds);

		Assertions.assertThat(repostSummaryList).hasSize(postIds.size());
		Assertions
			.assertThat(repostSummaryList.stream().filter(l -> l.userInteractions().isPresent()).count())
			.isEqualTo(0);
	}
}
