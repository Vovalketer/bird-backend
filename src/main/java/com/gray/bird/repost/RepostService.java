package com.gray.bird.repost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gray.bird.repost.dto.RepostSummary;
import com.gray.bird.repost.dto.RepostsCount;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RepostService {
	private final RepostRepository repo;

	@Transactional
	public void repost(UUID userId, Long postId) {
		RepostEntity repost = new RepostEntity(userId, postId);
		repo.save(repost);
	}

	@Transactional
	public void unrepost(UUID userId, Long postId) {
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

	public RepostSummary getRepostSummary(UUID userId, Long postId) {
		List<RepostEntity> reposts = repo.findByPostId(postId);
		Optional<RepostEntity> repost =
			reposts.stream().filter(l -> l.getId().getUserId().equals(userId)).findAny();

		return new RepostSummary(
			postId, reposts.size(), repost.isPresent(), repost.map(l -> l.getCreatedAt()).orElse(null));
	}

	public RepostSummary getRepostSummary(Long postId) {
		RepostsCount reposts = getRepostCountByPostId(postId);
		return new RepostSummary(postId, reposts.repostsCount(), false, null);
	}

	public List<RepostSummary> getRepostSummaryByPostIds(UUID userId, Collection<Long> postIds) {
		List<RepostEntity> allReposts = repo.findByPostIdsIn(postIds);
		Map<Long, List<RepostEntity>> repostsByPostId =
			allReposts.stream().collect(Collectors.groupingBy(like -> like.getId().getPostId()));

		List<RepostSummary> collect =
			postIds.stream()
				.map(postId -> {
					List<RepostEntity> reposts = repostsByPostId.getOrDefault(postId, new ArrayList<>());
					int repostsCount = reposts.size();
					Optional<RepostEntity> userRepost =
						reposts.stream().filter(like -> like.getId().getUserId().equals(userId)).findAny();
					boolean isReposted = userRepost.isPresent();
					LocalDateTime repostedAt = userRepost.map(RepostEntity::getCreatedAt).orElse(null);

					return new RepostSummary(postId, repostsCount, isReposted, repostedAt);
				})
				.collect(Collectors.toList());

		return collect;
	}

	public List<RepostSummary> getRepostSummaryByPostIds(Collection<Long> postIds) {
		List<RepostEntity> allReposts = repo.findByPostIdsIn(postIds);
		Map<Long, List<RepostEntity>> repostsByPostId =
			allReposts.stream().collect(Collectors.groupingBy(like -> like.getId().getPostId()));

		List<RepostSummary> collect = postIds.stream()
										  .map(postId -> {
											  List<RepostEntity> likes =
												  repostsByPostId.getOrDefault(postId, new ArrayList<>());
											  int repostsCount = likes.size();
											  return new RepostSummary(postId, repostsCount, false, null);
										  })
										  .collect(Collectors.toList());

		return collect;
	}
}
