package com.gray.bird.storage.exception;

public class EmptyFilenameException extends StorageException {
	public EmptyFilenameException() {
		super("Cannot save empty filename");
	}
}
