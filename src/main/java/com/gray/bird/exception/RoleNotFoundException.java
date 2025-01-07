package com.gray.bird.exception;

public class RoleNotFoundException extends RuntimeException {
	public RoleNotFoundException() {
		super("Role not found");
	}

	public RoleNotFoundException(String message) {
		super(message);
	}

	public RoleNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
