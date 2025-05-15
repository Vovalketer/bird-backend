package com.gray.bird.media.exception;

public class MediaProcessingException extends MediaException {
	public MediaProcessingException(String filename, Throwable cause) {
		super("Error processing media file: " + filename, cause);
	}

	public MediaProcessingException(String filename) {
		super("Error processing media file: " + filename);
	}
}
