package com.gray.bird.media;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

import com.gray.bird.media.dto.MediaRequest;

@Service
@Transactional(rollbackFor = Exception.class)
public class MediaCommandService {
	public Set<MediaEntity> uploadImages(MediaRequest mediaRequest) {
		if (mediaRequest != null) {
			return Collections.emptySet();
		} else {
			return Collections.emptySet();
		}
	}
}
