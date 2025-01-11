package com.gray.bird.repost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import com.gray.bird.exception.ResourceNotFoundException;
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
		return repo.countByPostId(id).orElseThrow(() -> new ResourceNotFoundException());
	}

	public List<RepostsCount> getRepostCountByPostIds(Iterable<Long> ids) {
		return repo.countByPostIdsIn(ids);
	}
}
