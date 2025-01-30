package com.gray.bird.user.dto;

import com.gray.bird.common.json.ResourceData;

public class UserResource extends ResourceData<String, UserAttributes, Void> {
	public UserResource(String id, UserAttributes attributes, UserRelationships relationships) {
		super(userType(), id, attributes, null);
	}

	public static String userType() {
		return "users";
	}
}
