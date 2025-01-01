package com.gray.bird.exception;

public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException() {
		super(ErrorMessages.NOT_FOUND);
	}
}
