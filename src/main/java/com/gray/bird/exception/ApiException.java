package com.gray.bird.exception;

/**
 * ApiException
 */
public class ApiException extends RuntimeException {

	public ApiException(String message) {
		super(message);
	}

	public ApiException() {
		super("There was an exception");
	}

}
