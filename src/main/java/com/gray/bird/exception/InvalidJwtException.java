package com.gray.bird.exception;

public class InvalidJwtException extends RuntimeException {
	public InvalidJwtException() {
		super(ErrorMessages.INVALID_TOKEN);
	}
	public InvalidJwtException(String message) {
		super(message);
	}
	public InvalidJwtException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
