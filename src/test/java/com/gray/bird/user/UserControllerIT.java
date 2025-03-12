package com.gray.bird.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gray.bird.auth.jwt.JwtService;
import com.gray.bird.common.ResourcePaths;
import com.gray.bird.testConfig.TestcontainersConfig;
import com.gray.bird.user.dto.UserCreationRequest;
import com.gray.bird.user.follow.FollowService;
import com.gray.bird.utils.TestUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Import(TestcontainersConfig.class)
@Sql(scripts = {"/sql/mockaroo/roles.sql",
		 "/sql/mockaroo/users.sql",
		 "/sql/mockaroo/posts.sql",
		 "/sql/mockaroo/timelines.sql"},
	executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/sql/teardown/roles.sql",
		 "/sql/teardown/users.sql",
		 "/sql/teardown/posts.sql",
		 "/sql/teardown/timelines.sql"},
	executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class UserControllerIT {
	@LocalServerPort
	private int port;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private JwtService jwtService;
	private TestUtils testUtils = new TestUtils();

	private String baseUrl = ResourcePaths.USERS;

	@Nested
	class UnauthenticatedUser {
		@Nested
		class GetUser {
			@Autowired
			private UserRepository userRepository;

			@Test
			@Rollback
			void shouldRetrieveAnUserResource() throws Exception {
				UserEntity userToSave = testUtils.createUser();
				userToSave.setId(null);
				UserEntity user = userRepository.save(userToSave);
				String username = user.getUsername();

				mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/{username}", username))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(user.getUuid().toString()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.attributes.username").value(username))
					.andExpect(
						MockMvcResultMatchers.jsonPath("$.data.attributes.handle").value(user.getHandle()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.attributes.dateOfBirth")
							.value(user.getDateOfBirth().toString()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.attributes.profileImage")
							.value(user.getProfileImage()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.attributes.bio").value(user.getBio()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.attributes.location")
							.value(user.getLocation()));
			}

			@Test
			void userNotFoundShouldThrow404() throws Exception {
				String username = "_nonExistentUser";

				mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/{username}", username))
					.andExpect(MockMvcResultMatchers.status().isNotFound());
			}
		}

		@Nested
		class GetPosts {
			@Test
			void shouldRetrieveUserPosts() throws Exception {
				String username = "mtompion1";

				mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/{username}/posts", username))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.length()", Matchers.greaterThan(0)));
			}

			@Test
			void shouldThrowWhenUserNotFound() throws Exception {
				String username = "_nonExistentUser";

				mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/{username}/posts", username))
					.andExpect(MockMvcResultMatchers.status().isNotFound());
			}
		}

		@Nested
		class Register {
			@Test
			@Rollback
			void shouldCreateUserGivenValidRegistrationData() throws Exception {
				UserCreationRequest userCreationRequest = UserCreationRequest.builder()
															  .username("testUsername")
															  .handle("testHandle")
															  .email("test@test.com")
															  .password("testPassword")
															  .build();

				mockMvc
					.perform(MockMvcRequestBuilders.post(baseUrl + "/register")
							.content(objectMapper.writeValueAsString(userCreationRequest))
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isCreated())
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.id").isNotEmpty())
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.attributes.username")
							.value(userCreationRequest.username()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.attributes.handle")
							.value(userCreationRequest.handle()));
			}

			@Test
			void shouldReturnAnErrorForEachGivenInvalidRegistrationData()
				throws JsonProcessingException, Exception {
				UserCreationRequest userCreationRequest = UserCreationRequest.builder()
															  .username("")
															  .email("invalidEmail.com")
															  .handle("")
															  .password("invpw")
															  .build();

				mockMvc
					.perform(MockMvcRequestBuilders.post(baseUrl + "/register")
							.content(objectMapper.writeValueAsString(userCreationRequest))
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.errors.size()").value(4));
			}

			@Test
			void shouldNotRegisterWithEmptyUsername() throws Exception {
				UserCreationRequest userCreationRequest = UserCreationRequest.builder()
															  .username("")
															  .email("valid@email.com")
															  .handle("validHandle")
															  .password("password")
															  .build();
				mockMvc
					.perform(MockMvcRequestBuilders.post(baseUrl + "/register")
							.content(objectMapper.writeValueAsString(userCreationRequest))
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.errors.size()").value(1))
					.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].status").value("400"))
					.andExpect(
						MockMvcResultMatchers.jsonPath("$.errors[0].source.parameter").value("username"));
			}

			@Test
			void shouldNotRegisterWithInvalidEmail() throws Exception {
				UserCreationRequest userCreationRequest = UserCreationRequest.builder()
															  .username("someUser")
															  .email("invalidEmail")
															  .handle("validHandle")
															  .password("validPassword")
															  .build();
				mockMvc
					.perform(MockMvcRequestBuilders.post(baseUrl + "/register")
							.content(objectMapper.writeValueAsString(userCreationRequest))
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.errors.size()").value(1))
					.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].status").value("400"))
					.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].source.parameter").value("email"));
			}

			@Test
			void shouldNotRegisterWithEmptyHandle() throws Exception {
				UserCreationRequest userCreationRequest = UserCreationRequest.builder()
															  .username("someUser")
															  .email("valid@email.com")
															  .handle("")
															  .password("validPassword")
															  .build();
				mockMvc
					.perform(MockMvcRequestBuilders.post(baseUrl + "/register")
							.content(objectMapper.writeValueAsString(userCreationRequest))
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.errors.size()").value(1))
					.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].status").value("400"))
					.andExpect(
						MockMvcResultMatchers.jsonPath("$.errors[0].source.parameter").value("handle"));
			}

			@Test
			void shouldNotRegisterWithShortPassword() throws Exception {
				UserCreationRequest userCreationRequest = UserCreationRequest.builder()
															  .username("someUser")
															  .email("valid@email.com")
															  .handle("validHandle")
															  .password("short")
															  .build();
				mockMvc
					.perform(MockMvcRequestBuilders.post(baseUrl + "/register")
							.content(objectMapper.writeValueAsString(userCreationRequest))
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.errors.size()").value(1))
					.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].status").value("400"))
					.andExpect(
						MockMvcResultMatchers.jsonPath("$.errors[0].source.parameter").value("password"));
			}
		}

		@Nested
		class Following {
			@Test
			void shouldNotFollowAnotherUser() throws Exception {
				String username = "someUser";
				mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/{username}/following", username))
					.andExpect(MockMvcResultMatchers.status().isUnauthorized());
			}

			@Test
			void shouldNotUnfollowAnotherUser() throws Exception {
				String username = "someUser";
				mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/{username}/following", username))
					.andExpect(MockMvcResultMatchers.status().isUnauthorized());
			}
		}

		@Nested
		class GetHomeTimeline {
			@Test
			void shouldReturnHomeTimeline() throws Exception {
				String username = "mtompion1";

				mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/{username}/timeline", username))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.length()", Matchers.greaterThan(0)));
			}

			@Test
			void shouldReturnNotFoundWhenUserIsNotFound() throws Exception {
				String username = "_nonExistentUser";

				mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/{username}/timelne", username))
					.andExpect(MockMvcResultMatchers.status().isNotFound());
			}
		}
	}

	@Nested
	class LoggedInUser {
		private UserEntity user;
		private UserEntity randomUser;
		private UUID userId;
		private String accessToken = "Bearer accessToken";

		@Autowired
		private UserRepository userRepository;
		@Autowired
		private FollowService followService;

		@BeforeEach
		void setUp() {
			List<UserEntity> users = userRepository.findAll(PageRequest.of(0, 2)).getContent();
			user = users.get(0);
			randomUser = users.get(1);
			userId = user.getUuid();
			Authentication authentication = new TestingAuthenticationToken(userId, "test_password", "USER");

			Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(true);
			Mockito.when(jwtService.getAuthenticationFromAccessToken(Mockito.anyString()))
				.thenReturn(authentication);
		}

		@Nested
		class GetUser {
			@Test
			void shouldReturnUserProfileOfTheCurrentUser() throws Exception {
				mockMvc
					.perform(
						MockMvcRequestBuilders.get(baseUrl).header(HttpHeaders.AUTHORIZATION, accessToken))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(user.getUuid().toString()));
			}

			@Test
			void shouldReturnUnauthorizedWhenNotLoggedIn() throws Exception {
				mockMvc.perform(MockMvcRequestBuilders.get(baseUrl))
					.andExpect(MockMvcResultMatchers.status().isNotFound());
			}
		}

		@Nested
		class GetPosts {
			@Test
			void shouldReturnUserPosts() throws Exception {
				String username = "mtompion1";

				mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/{username}/posts", username))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.length()", Matchers.greaterThan(0)));
			}

			@Test
			void shouldReturnNotFoundWhenUserIsNotFound() throws Exception {
				String username = "_nonExistentUser";

				mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/{username}/posts", username))
					.andExpect(MockMvcResultMatchers.status().isNotFound());
			}
		}

		@Nested
		class Following {
			@Test
			@Transactional
			@Rollback
			void shouldFollowAnotherUser() throws Exception {
				String username = randomUser.getUsername();
				mockMvc
					.perform(MockMvcRequestBuilders.post(baseUrl + "/{username}/following", username)
							.header(HttpHeaders.AUTHORIZATION, accessToken))
					.andExpect(MockMvcResultMatchers.status().isOk());
			}

			@Test
			@Transactional
			@Rollback
			void shouldUnfollowAnotherUser() throws Exception {
				String username = randomUser.getUsername();
				followService.followUser(userId, randomUser.getUuid());
				mockMvc
					.perform(MockMvcRequestBuilders.delete(baseUrl + "/{username}/following", username)
							.header(HttpHeaders.AUTHORIZATION, accessToken))
					.andExpect(MockMvcResultMatchers.status().isOk());
			}
		}
	}
}
