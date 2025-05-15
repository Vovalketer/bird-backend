package com.gray.bird.media.exception;

public class MediaLimitExceededException extends MediaException {
	public MediaLimitExceededException(int limit) {
		super("Request exceeds media limit: " + limit);
	}
}
