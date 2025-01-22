package com.gray.bird.exception;

public class InvalidVerificationTokenException extends RuntimeException {
	public InvalidVerificationTokenException() {
		super(ErrorMessages.INVALID_TOKEN);
	}

	public InvalidVerificationTokenException(String message) {
		super(message);
	}

	public InvalidVerificationTokenException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
