package com.gray.bird.media.exception;

public class MediaNotFoundException extends MediaException {
	public MediaNotFoundException(String filename) {
		super(String.format("Media with filename %s not found", filename));
	}
}
