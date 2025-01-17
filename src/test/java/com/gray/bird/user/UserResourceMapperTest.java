package com.gray.bird.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceFactory;
import com.gray.bird.config.ObjectMapperConfig;
import com.gray.bird.user.dto.UserProjection;
import com.gray.bird.utils.TestUtils;

@SpringBootTest(classes = {UserResourceMapper.class, ResourceFactory.class, ObjectMapperConfig.class})
public class UserResourceMapperTest {
	@Autowired
	private UserResourceMapper userResourceMapper;

	private TestUtils testUtils = new TestUtils();

	@Test
	void testToResource() {
		UserProjection userProjection = testUtils.createUserProjection();

		ResourceData resource = userResourceMapper.toResource(userProjection);

		Assertions.assertThat(resource).isNotNull();
		Assertions.assertThat(resource.getId()).isEqualTo(userProjection.userId().toString());
		Assertions.assertThat(resource.getType()).isEqualTo("user");
		Assertions.assertThat(resource.getAttributes()).isNotNull();
		Assertions.assertThat(resource.getAttribute("username", String.class))
			.isEqualTo(userProjection.username());
		Assertions.assertThat(resource.getAttribute("handle", String.class))
			.isEqualTo(userProjection.handle());
		Assertions.assertThat(resource.getAttribute("bio", String.class)).isEqualTo(userProjection.bio());
		Assertions.assertThat(resource.getAttribute("location", String.class))
			.isEqualTo(userProjection.location());
		Assertions.assertThat(resource.getAttribute("profileImage", String.class))
			.isEqualTo(userProjection.profileImage());
		// dates are being converted into human readable strings by ObjectMapper
		Assertions.assertThat(resource.getAttribute("createdAt"))
			.isEqualTo(userProjection.createdAt().toString());
		Assertions.assertThat(resource.getAttribute("dateOfBirth"))
			.isEqualTo(userProjection.dateOfBirth().toString());
	}
}
