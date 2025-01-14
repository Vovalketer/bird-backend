package com.gray.bird.user;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.gray.bird.common.jsonApi.ResourceAttributes;
import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceDataMapper;
import com.gray.bird.common.jsonApi.ResourceFactory;
import com.gray.bird.common.jsonApi.ResourceIdentifier;
import com.gray.bird.user.dto.UserAttributes;
import com.gray.bird.user.dto.UserProjection;

@Component
@RequiredArgsConstructor
public class UserResourceMapper implements ResourceDataMapper<UserProjection> {
	private final ResourceFactory resourceFactory;

	@Override
	public ResourceData toResource(UserProjection data) {
		UserAttributes userAttributes = getUserAttributes(data);
		ResourceAttributes attributes = resourceFactory.createAttributes(userAttributes);
		ResourceIdentifier identifier = resourceFactory.createIdentifier("user", data.userId().toString());
		return resourceFactory.createData(identifier, attributes);
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
