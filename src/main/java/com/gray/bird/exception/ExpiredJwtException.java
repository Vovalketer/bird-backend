package com.gray.bird.exception;

public class ExpiredJwtException extends RuntimeException {
	public ExpiredJwtException() {
		super(ErrorMessages.EXPIRED_TOKEN);
	}
	public ExpiredJwtException(String message) {
		super(message);
	}
}
