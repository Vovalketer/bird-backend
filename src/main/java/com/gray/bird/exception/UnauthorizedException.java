package com.gray.bird.exception;

public class UnauthorizedException extends RuntimeException {
	public UnauthorizedException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public UnauthorizedException(String message) {
		super(message);
	}

	public UnauthorizedException() {
		super(ErrorMessages.UNAUTHORIZED);
	}
}
