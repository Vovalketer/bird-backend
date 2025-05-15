package com.gray.bird.media.exception;

public class MalformedContentTypeException extends MediaException {
	public MalformedContentTypeException(String contentType, Throwable cause) {
		super("Unable to parse content type: " + contentType, cause);
	}
}
