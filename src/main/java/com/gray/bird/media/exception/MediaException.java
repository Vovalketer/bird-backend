package com.gray.bird.media.exception;

public class MediaException extends RuntimeException {
	public MediaException(String message, Throwable cause) {
		super(message, cause);
	}

	public MediaException(String message) {
		super(message);
	}
}
