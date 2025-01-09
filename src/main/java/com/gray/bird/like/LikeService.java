package com.gray.bird.like;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {
	private final LikeRepository repo;

	public Page<Long> getLikingUsers(Long postId, Pageable pageable) {
		return repo.findUsersLikingPostId(postId, pageable);
	}

	public Page<Long> getLikedByUserId(Long userId, Pageable pageable) {
		return repo.findLikedPostsByUserId(userId, pageable);
	}

	@Transactional
	public void likePost(UUID userId, Long postId) {
		LikeEntity like = new LikeEntity(userId, postId);
		repo.save(like);
	}

	@Transactional
	public void unlikePost(UUID userId, Long postId) {
		LikeEntity like = new LikeEntity(userId, postId);
		repo.delete(like);
	}
}
