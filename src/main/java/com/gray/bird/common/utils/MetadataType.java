package com.gray.bird.common.utils;

import lombok.Getter;

@Getter
public enum MetadataType {
	PAGINATION("pagination");

	private String value;

	MetadataType(String value) {
		this.value = value;
	}
}
