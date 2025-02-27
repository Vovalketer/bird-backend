package com.gray.bird.like;

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

import com.gray.bird.like.dto.LikeSummary;
import com.gray.bird.like.dto.LikeUserInteractions;
import com.gray.bird.like.dto.LikesCount;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {
	private final LikeRepository repo;

	@Transactional
	public void likePost(UUID userId, Long postId) {
		LikeEntity like = new LikeEntity(userId, postId);
		repo.save(like);
	}

	@Transactional
	public void unlikePost(UUID userId, Long postId) {
		LikeId likeId = new LikeId(userId, postId);
		repo.deleteById(likeId);
	}

	public Page<Long> getLikingUserIdsByPostId(Long postId, Pageable pageable) {
		return repo.findUsersLikingPostId(postId, pageable);
	}

	public Page<Long> getLikedPostIdsByUserId(UUID userId, Pageable pageable) {
		return repo.findLikedPostsByUserId(userId, pageable);
	}

	public LikesCount getLikesCountByPostId(Long postId) {
		return repo.countByPostId(postId).orElse(new LikesCount(postId, 0L));
	}

	public List<LikesCount> getLikesCountByPostIds(Iterable<Long> postids) {
		return repo.countByPostIdsIn(postids);
	}

	public LikeSummary getLikeSummary(UUID userId, Long postId) {
		List<LikeEntity> likes = repo.findByPostId(postId);
		Optional<LikeUserInteractions> userInteractions = getUserInteractions(likes, userId);
		return new LikeSummary(postId, likes.size(), userInteractions);
	}

	public List<LikeSummary> getLikeSummaryByPostIds(UUID userId, Collection<Long> postIds) {
		List<LikeEntity> allLikes = repo.findByPostIdsIn(postIds);
		Map<Long, List<LikeEntity>> likesByPostId =
			allLikes.stream().collect(Collectors.groupingBy(like -> like.getId().getPostId()));

		List<LikeSummary> collect =
			postIds.stream()
				.map(postId -> {
					List<LikeEntity> likes = likesByPostId.getOrDefault(postId, new ArrayList<>());
					int likesCount = likes.size();
					Optional<LikeUserInteractions> userInteractions = getUserInteractions(likes, userId);
					return new LikeSummary(postId, likesCount, userInteractions);
				})
				.collect(Collectors.toList());

		return collect;
	}

	private Optional<LikeUserInteractions> getUserInteractions(List<LikeEntity> likes, UUID userId) {
		if (userId == null) {
			return Optional.empty();
		}
		Optional<LikeEntity> like =
			likes.stream().filter(_like -> _like.getId().getUserId().equals(userId)).findAny();
		LikeUserInteractions interactions = like.map(l -> new LikeUserInteractions(true, l.getCreatedAt()))
												.orElse(new LikeUserInteractions(false, null));
		return Optional.of(interactions);
	}
}
