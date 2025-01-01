package com.gray.bird.media;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.gray.bird.media.dto.MediaProjection;

@Service
@RequiredArgsConstructor
public class MediaQueryService {
	private final MediaRepository mediaRepository;
	public List<MediaProjection> getMediaByPostId(Long postId) {
		return mediaRepository.findByPostId(postId, MediaProjection.class);
	}
	public List<MediaProjection> getAllMediaByPostId(Iterable<Long> postIds) {
		return mediaRepository.findAllByPostIdIn(postIds, MediaProjection.class);
	}
}
