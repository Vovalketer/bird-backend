package com.gray.bird.media.exception;

public class InvalidMetadataException extends MediaException {
	public InvalidMetadataException(String field, String filename) {
		super("Invalid metadata for media file: " + filename + ", field: " + field);
	}
}
