package com.gray.bird.like;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

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
}
