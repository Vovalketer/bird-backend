package com.gray.bird.auth.jwt;

public enum TokenType {
	ACCESS("access_token"),
	REFRESH("refresh_token");

	private final String value;

	TokenType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
