package com.gray.bird.user.dto;

import com.gray.bird.common.ResourcePaths;
import com.gray.bird.common.ResourceType;
import com.gray.bird.common.json.ResourceData;

public class UserResource extends ResourceData<String, UserAttributes, Void> {
	public UserResource(String id, UserAttributes attributes, UserRelationships relationships) {
		super(userType(), id, attributes, null);
		super.getLinks().setSelf(ResourcePaths.USERS + "/" + attributes.username());
	}

	public static String userType() {
		return ResourceType.USERS.getType();
	}
}
