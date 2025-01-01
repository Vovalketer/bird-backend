package com.gray.bird.media.view;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaViewService {
	private final MediaViewRepository mediaViewRepository;

	public List<MediaView> getPostMedia(Long postId) {
		return mediaViewRepository.findByPostId(postId);
	}

	public List<MediaView> getAllPostMediaById(Iterable<Long> postIds) {
		return mediaViewRepository.findAllByPostId(postIds);
	}
}
