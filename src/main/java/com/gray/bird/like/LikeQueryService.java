package com.gray.bird.like;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.gray.bird.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeQueryService {
	private final LikeRepository likesRepository;

	public Page<Long> getLikingUserIdsByPostId(Long postId, Pageable pageable) {
		return likesRepository.findUsersLikingPostId(postId, pageable);
	}

	public Page<Long> getLikedPostIdsByUserId(Long userId, Pageable pageable) {
		return likesRepository.findLikedPostsByUserId(userId, pageable);
	}

	public Long getLikesCountByPostId(Long postId) {
		return likesRepository.countByPostId(postId).orElseThrow(() -> new ResourceNotFoundException());
	}
}
