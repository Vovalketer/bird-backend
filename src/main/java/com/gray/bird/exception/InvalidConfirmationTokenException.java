package com.gray.bird.exception;

public class InvalidConfirmationTokenException extends RuntimeException {
	public InvalidConfirmationTokenException() {
		super(ErrorMessages.INVALID_TOKEN);
	}
	public InvalidConfirmationTokenException(String message) {
		super(message);
	}
	public InvalidConfirmationTokenException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
