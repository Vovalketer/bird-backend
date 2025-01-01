package com.gray.bird.common;

public enum HttpResponseType {
	SUCCESS("success"),
	FAILURE("failure"),
	NOT_FOUND("not found"),
	UNAUTHORIZED("unauthorized");

	private String value;

	public String getValue() {
		return value;
	}

	HttpResponseType(String value) {
		this.value = value;
	}
}
