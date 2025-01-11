package com.gray.bird.user;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import com.gray.bird.common.jsonApi.ResourceAttributes;
import com.gray.bird.common.jsonApi.ResourceCollectionAggregate;
import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceIdentifier;
import com.gray.bird.common.jsonApi.ResourceSingleAggregate;
import com.gray.bird.common.utils.ResourceFactory;
import com.gray.bird.user.dto.UserAttributes;
import com.gray.bird.user.dto.UserProjection;

@Component
@RequiredArgsConstructor
public class UserResourceConverter {
	private final ResourceFactory resourceFactory;

	public ResourceData toResource(UserProjection user) {
		UserAttributes userAttributes = getUserAttributes(user);
		ResourceIdentifier identifier = resourceFactory.createIdentifier("user", user.userId().toString());
		ResourceAttributes attributes = resourceFactory.createAttributes(userAttributes);
		ResourceData content = resourceFactory.createContent(identifier, attributes);
		return content;
	}

	public List<ResourceData> toResource(List<UserProjection> users) {
		return users.stream().map(p -> toResource(p)).collect(Collectors.toList());
	}

	private UserAttributes getUserAttributes(UserProjection user) {
		return UserAttributes.builder()
			.profileImage(user.profileImage())
			.location(user.location())
			.username(user.username())
			.bio(user.bio())
			.handle(user.handle())
			.createdAt(user.createdAt())
			.dateOfBirth(user.dateOfBirth())
			.build();
	}

	public ResourceSingleAggregate toAggregate(UserProjection user) {
		ResourceData resource = toResource(user);
		ResourceSingleAggregate aggregate = resourceFactory.createSingleAggregate(resource);
		return aggregate;
	}

	public ResourceCollectionAggregate toAggregate(List<UserProjection> users) {
		List<ResourceData> resource = toResource(users);
		return resourceFactory.createCollectionAggregate(resource);
	}
}
