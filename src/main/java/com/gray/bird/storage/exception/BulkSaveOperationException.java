package com.gray.bird.storage.exception;

public class BulkSaveOperationException extends StorageException {
	public BulkSaveOperationException(Throwable cause) {
		super("Failed to save all files", cause);
	}
}
