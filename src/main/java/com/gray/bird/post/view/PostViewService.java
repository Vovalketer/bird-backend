package com.gray.bird.post.view;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.gray.bird.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class PostViewService {
	private final PostViewRepository postViewRepository;

	public PostView getByPostId(Long id) {
		return postViewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
	}

	public List<PostView> getByPostIds(Iterable<Long> ids) {
		return postViewRepository.findAllByIdIn(ids);
	}

	public List<Long> getReplyIdsByPostId(Long postId) {
		return postViewRepository.findRepliesIdByParentId(postId);
	}

	public Page<Long> getReplyIdsByPostId(Long postId, Pageable pageable) {
		return postViewRepository.findRepliesIdByParentId(postId, pageable);
	}
	// DEPRECATED ̉↓

	public Page<PostView> getByUserId(Long userId, Pageable pageable) {
		return postViewRepository.findByUserId(userId, pageable);
	}

	public Page<PostView> getUserTimelineByUserId(Long id, Pageable pageable) {
		return postViewRepository.findUserTimelineByUserId(id, pageable);
	}

	public Page<PostView> getRepliesByPostId(Long parentPostId, Pageable pageable) {
		return postViewRepository.findRepliesByParentId(parentPostId, pageable);
	}
}
