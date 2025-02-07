package com.gray.bird.timeline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import com.gray.bird.auth.jwt.JwtService;
import com.gray.bird.common.ResourcePaths;
import com.gray.bird.testConfig.TestcontainersConfig;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestcontainersConfig.class)
@Sql(scripts = {"/sql/mockaroo/roles.sql",
		 "/sql/mockaroo/users.sql",
		 "/sql/mockaroo/posts.sql",
		 "/sql/mockaroo/timelines.sql",
		 "/sql/mockaroo/follows.sql"})
public class TimelineControllerIT {
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private JwtService jwtService;

	private String baseUrl = ResourcePaths.FEEDS;

	@Nested
	class AuthenticatedUser {
		// our trusty friend "mtompion1"
		// private UUID USER_ID = UUID.fromString("4e27b52d-b86c-448e-9e9e-db1d38c9a55c");
		private UUID USER_ID = UUID.fromString("f7caceb3-a9dd-4cf2-8fde-7576bb7d0b2d");
		private String ACCESS_TOKEN = "Bearer accessToken";

		@BeforeEach
		void setUp() {
			Authentication authentication = new TestingAuthenticationToken(USER_ID, "password", "USER");
			Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(true);
			Mockito.when(jwtService.getAuthenticationFromAccessToken(Mockito.anyString()))
				.thenReturn(authentication);
		}

		@Test
		void shouldFollowingFeedWhenRequested() throws Exception {
			mockMvc
				.perform(MockMvcRequestBuilders.get(baseUrl + "/following")
						.header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.data.length()", Matchers.greaterThan(0)))
				.andExpect(
					MockMvcResultMatchers.jsonPath("$.included.users.length()", Matchers.greaterThan(0)));
		}
	}

	@Nested
	class UnauthenticatedUser {
		@Test
		void shouldReturnUnauthorizedWhenFeedIsRequested() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/following"))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		}
	}
}
