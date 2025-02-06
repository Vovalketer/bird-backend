package com.gray.bird.timeline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.gray.bird.common.ResourcePaths;
import com.gray.bird.testConfig.TestcontainersConfig;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestcontainersConfig.class)
@Sql(scripts = {"/sql/mockaroo/roles.sql",
		 "/sql/mockaroo/users.sql",
		 "/sql/mockaroo/posts.sql",
		 "/sql/mockaroo/timelines.sql"})
public class TimelineControllerIT {
	@Autowired
	private MockMvc mockMvc;

	private String baseUrl = ResourcePaths.USERS_USERNAME_TIMELINES;

	@Nested
	class UnauthenticatedUser {}
}
