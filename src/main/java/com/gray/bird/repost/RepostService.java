package com.gray.bird.repost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gray.bird.repost.dto.RepostSummary;
import com.gray.bird.repost.dto.RepostUserInteractions;
import com.gray.bird.repost.dto.RepostsCount;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RepostService {
	private final RepostRepository repo;

	@Transactional
	public void repost(Long postId, UUID userId) {
		RepostEntity repost = new RepostEntity(userId, postId);
		repo.save(repost);
	}

	@Transactional
	public void unrepost(Long postId, UUID userId) {
		RepostEntity repost = new RepostEntity(userId, postId);
		repo.delete(repost);
	}

	public Page<Long> getRepostIdsByUserId(UUID userId, Pageable pageable) {
		return repo.findRepostsByUserId(userId, pageable);
	}

	public Page<Long> getRepostingUserIdsByPostId(Long postId, Pageable pageable) {
		return repo.findUsersRepostingByPostId(postId, pageable);
	}

	public RepostsCount getRepostCountByPostId(Long id) {
		return repo.countByPostId(id).orElse(new RepostsCount(id, 0L));
	}

	public List<RepostsCount> getRepostCountByPostIds(Iterable<Long> ids) {
		return repo.countByPostIdsIn(ids);
	}

	public RepostSummary getRepostSummary(Long postId, UUID userId) {
		List<RepostEntity> reposts = repo.findByPostId(postId);
		Optional<RepostUserInteractions> userInteractions = getUserInteractions(reposts, userId);

		return new RepostSummary(postId, reposts.size(), userInteractions);
	}

	public List<RepostSummary> getRepostSummaryByPostIds(Collection<Long> postIds, UUID userId) {
		List<RepostEntity> allReposts = repo.findByPostIdsIn(postIds);
		Map<Long, List<RepostEntity>> repostsByPostId =
			allReposts.stream().collect(Collectors.groupingBy(like -> like.getId().getPostId()));

		List<RepostSummary> collect =
			postIds.stream()
				.map(postId -> {
					List<RepostEntity> reposts = repostsByPostId.getOrDefault(postId, new ArrayList<>());
					int repostsCount = reposts.size();
					Optional<RepostUserInteractions> userInteractions = getUserInteractions(reposts, userId);

					return new RepostSummary(postId, repostsCount, userInteractions);
				})
				.collect(Collectors.toList());

		return collect;
	}

	private Optional<RepostUserInteractions> getUserInteractions(List<RepostEntity> reposts, UUID userId) {
		if (userId == null) {
			return Optional.empty();
		}
		Optional<RepostEntity> repost =
			reposts.stream().filter(r -> r.getId().getUserId().equals(userId)).findAny();
		RepostUserInteractions interactions =
			repost.map(r -> new RepostUserInteractions(true, r.getRepostedAt()))
				.orElse(new RepostUserInteractions(false, null));
		return Optional.of(interactions);
	}
}
