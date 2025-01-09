package com.gray.bird.repost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import com.gray.bird.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class RepostQueryService {
	private final RepostRepository repostRepository;

	public Page<Long> getRepostIdsByUserId(UUID userId, Pageable pageable) {
		return repostRepository.findRepostsByUserId(userId, pageable);
	}

	public Page<Long> getRepostingUserIdsByPostId(Long postId, Pageable pageable) {
		return repostRepository.findUsersRepostingByPostId(postId, pageable);
	}

	public Long getRepostCountByPostId(Long id) {
		return repostRepository.countByPostId(id).orElseThrow(() -> new ResourceNotFoundException());
	}
}
