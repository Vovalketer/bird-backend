package com.gray.bird.storage.exception;

public class FilenameAlreadyExistsException extends StorageException {
	public FilenameAlreadyExistsException(String filename) {
		super("Filename already exists: " + filename);
	}
}
