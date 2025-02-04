package com.gray.bird.user;

import org.springframework.stereotype.Component;

import com.gray.bird.common.json.ResourceMapper;
import com.gray.bird.user.dto.UserAttributes;
import com.gray.bird.user.dto.UserProjection;
import com.gray.bird.user.dto.UserRelationships;
import com.gray.bird.user.dto.UserResource;

@Component
public class UserResourceMapper implements ResourceMapper<UserProjection, UserResource> {
	@Override
	public UserResource toResource(UserProjection data) {
		UserAttributes userAttributes = getUserAttributes(data);
		UserRelationships relationships = new UserRelationships();
		UserResource resource = new UserResource(data.uuid().toString(), userAttributes, relationships);
		return resource;
	}

	private UserAttributes getUserAttributes(UserProjection data) {
		return UserAttributes.builder()
			.profileImage(data.profileImage())
			.location(data.location())
			.username(data.username())
			.bio(data.bio())
			.handle(data.handle())
			.createdAt(data.createdAt())
			.dateOfBirth(data.dateOfBirth())
			.build();
	}
}
