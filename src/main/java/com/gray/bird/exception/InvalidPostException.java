package com.gray.bird.exception;

public class InvalidPostException extends RuntimeException {
	public InvalidPostException() {
		super(ErrorMessages.INVALID_POST);
	}

	public InvalidPostException(String message) {
		super(message);
	}

	public InvalidPostException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
