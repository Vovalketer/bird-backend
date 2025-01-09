package com.gray.bird.repost;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RepostManager {
	private final RepostRepository repo;

	public void repost(UUID userId, Long postId) {
		RepostEntity repost = new RepostEntity(userId, postId);
		repo.save(repost);
	}

	public void unrepost(UUID userId, Long postId) {
		RepostEntity repost = new RepostEntity(userId, postId);
		repo.delete(repost);
	}
}
