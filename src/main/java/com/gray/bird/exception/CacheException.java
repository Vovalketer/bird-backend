package com.gray.bird.exception;

public class CacheException extends RuntimeException {
	public CacheException() {
		super("An error has occurred with the cache");
	}

	public CacheException(String message) {
		super(message);
	}
	public CacheException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
