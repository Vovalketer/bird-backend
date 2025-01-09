package com.gray.bird.like;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeCommandService {
	private final LikeRepository repo;

	public void likePost(UUID userId, Long postId) {
		LikeEntity like = new LikeEntity(userId, postId);
		repo.save(like);
	}

	public void unlikePost(UUID userId, Long postId) {
		LikeId likeId = new LikeId(userId, postId);
		repo.deleteById(likeId);
	}
}
