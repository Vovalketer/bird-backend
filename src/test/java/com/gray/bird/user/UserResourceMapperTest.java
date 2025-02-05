package com.gray.bird.user;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.gray.bird.common.ResourceType;
import com.gray.bird.user.dto.UserProjection;
import com.gray.bird.user.dto.UserResource;
import com.gray.bird.utils.TestUtils;

@ExtendWith(SpringExtension.class)
public class UserResourceMapperTest {
	private UserResourceMapper userResourceMapper = new UserResourceMapper();
	private TestUtils testUtils = new TestUtils();

	@Test
	void testToResource() {
		UserProjection userProjection = testUtils.createUserProjection();

		UserResource resource = userResourceMapper.toResource(userProjection);
		Assertions.assertThat(resource).isNotNull();
		// id
		Assertions.assertThat(resource.getType()).isEqualTo(ResourceType.USERS.getType());
		Assertions.assertThat(resource.getId()).isEqualTo(userProjection.uuid().toString());
		// attributes
		Assertions.assertThat(resource.getAttributes()).isNotNull();
		Assertions.assertThat(resource.getAttributes().username()).isEqualTo(userProjection.username());
		Assertions.assertThat(resource.getAttributes().handle()).isEqualTo(userProjection.handle());
		Assertions.assertThat(resource.getAttributes().dateOfBirth()).isEqualTo(userProjection.dateOfBirth());
		Assertions.assertThat(resource.getAttributes().profileImage())
			.isEqualTo(userProjection.profileImage());
		Assertions.assertThat(resource.getAttributes().bio()).isEqualTo(userProjection.bio());
		Assertions.assertThat(resource.getAttributes().location()).isEqualTo(userProjection.location());
		Assertions.assertThat(resource.getAttributes().createdAt()).isEqualTo(userProjection.createdAt());
		Assertions.assertThat(resource.getRelationships()).isNull();
	}
}
