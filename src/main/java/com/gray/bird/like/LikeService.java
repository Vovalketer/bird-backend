package com.gray.bird.like;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.gray.bird.post.PostEntity;
import com.gray.bird.user.UserEntity;

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
	public void likePost(UserEntity user, PostEntity post) {
		LikeEntity like = new LikeEntity(user, post);
		repo.save(like);
	}

	@Transactional
	public void unlikePost(UserEntity user, PostEntity post) {
		LikeEntity like = new LikeEntity(user, post);
		repo.delete(like);
	}
}
