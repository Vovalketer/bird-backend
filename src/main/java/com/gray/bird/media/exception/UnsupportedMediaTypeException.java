package com.gray.bird.media.exception;

public class UnsupportedMediaTypeException extends MediaException {
	public UnsupportedMediaTypeException(String mediaType) {
		super("Unsupported media type: " + mediaType);
	}
}
