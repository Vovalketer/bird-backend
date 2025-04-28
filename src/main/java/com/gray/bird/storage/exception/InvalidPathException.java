package com.gray.bird.storage.exception;

public class InvalidPathException extends StorageException {
	public InvalidPathException(String path) {
		super("Invalid path: " + path);
	}
}
