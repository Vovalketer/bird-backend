package com.gray.bird.storage.exception;

public class DirectoryCreationException extends StorageException {
	public DirectoryCreationException(String path, Throwable cause) {
		super("Failed to create directory at path: " + path, cause);
	}
}
