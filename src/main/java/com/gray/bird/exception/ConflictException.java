package com.gray.bird.exception;

public class ConflictException extends RuntimeException {
	public ConflictException() {
		super(ErrorMessages.CONFLICT);
	}
	public ConflictException(String message) {
		super(message);
	}
	public ConflictException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
