package com.gray.bird.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.gray.bird.exception.ResourceNotFoundException;
import com.gray.bird.post.dto.PostProjection;

@Service
@RequiredArgsConstructor
public class PostQueryService {
	private final PostRepository postRepository;

	public PostProjection getPostById(Long id) {
		return postRepository.findById(id, PostProjection.class)
			.orElseThrow(() -> new ResourceNotFoundException());
	}

	public List<PostProjection> getAllPostsById(Iterable<Long> ids) {
		return postRepository.findAllByIdIn(ids, PostProjection.class);
	}

	public Page<Long> getReplyIds(Long postId, Pageable pageable) {
		return postRepository.findRepliesByParentPostId(postId, pageable);
	}

	public Long getRepliesCountByPostId(Long id) {
		return postRepository.countRepliesByPostId(id).orElseThrow(() -> new ResourceNotFoundException());
	}

	public Page<Long> getPostIdsByUsername(String username, Pageable pageable) {
		return postRepository.findPostIdsByUsername(username, pageable);
	}
}
