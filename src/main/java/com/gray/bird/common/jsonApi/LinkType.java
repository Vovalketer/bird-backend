package com.gray.bird.common.jsonApi;

public enum LinkType {
	SELF("self"),
	RELATED("related"),
	FIRST("first"),
	LAST("last"),
	PREV("prev"),
	NEXT("next");

	private String value;

	LinkType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
