package com.gray.bird.media.exception;

public class DuplicateFilenameException extends MediaException {
	public DuplicateFilenameException(String filename) {
		super("Duplicate filename: " + filename);
	}
}
