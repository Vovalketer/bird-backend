package com.gray.bird.storage.exception;

public class FileSaveException extends StorageException {
	public FileSaveException(String filename, Throwable cause) {
		super("Failed to save file: " + filename, cause);
	}
}
