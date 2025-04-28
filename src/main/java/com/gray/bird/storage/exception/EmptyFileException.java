package com.gray.bird.storage.exception;

public class EmptyFileException extends StorageException {
	public EmptyFileException(String filename) {
		super("Cannot save empty file: " + filename);
	}
}
