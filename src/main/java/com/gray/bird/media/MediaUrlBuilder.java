package com.gray.bird.media;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@Component
class MediaUrlBuilder {
	public String buildMediaUrl(String filename) {
		return MvcUriComponentsBuilder
			.fromMethodCall(MvcUriComponentsBuilder.on(MediaController.class).getMedia(filename))
			.build()
			.toUriString();
	}
}
