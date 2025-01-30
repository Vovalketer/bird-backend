package com.gray.bird.common;

import lombok.Getter;

@Getter
public enum ResourceType {
	USERS("users"),
	MEDIA("media"),
	POSTS("posts");

	private String type;
	ResourceType(String type) {
		this.type = type;
	}
}
