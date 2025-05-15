package com.gray.bird.media.exception;

public class MediaDatabaseException extends RuntimeException {
	public MediaDatabaseException(Throwable throwable) {
		super("Failed to save media to database", throwable);
	}
}
