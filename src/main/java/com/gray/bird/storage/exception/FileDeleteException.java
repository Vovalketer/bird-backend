package com.gray.bird.storage.exception;

public class FileDeleteException extends StorageException {
	public FileDeleteException(String filename, Throwable cause) {
		super("Failed to delete file: " + filename, cause);
	}
}
