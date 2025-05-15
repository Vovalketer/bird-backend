package com.gray.bird.media;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.gray.bird.media.dto.MediaDto;

@Service
@RequiredArgsConstructor
public class MediaQueryService {
	private final MediaRepository mediaRepository;

	public List<MediaDto> getMediaByPostId(Long postId) {
		return mediaRepository.findByPostId(postId, MediaDto.class);
	}

	public List<MediaDto> getAllMediaByPostId(Iterable<Long> postIds) {
		return mediaRepository.findAllByPostIdIn(postIds, MediaDto.class);
	}
}
