package com.gray.bird.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
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

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gray.bird.auth.jwt.JwtService;
import com.gray.bird.common.ResourcePaths;
import com.gray.bird.common.ResourceType;
import com.gray.bird.post.dto.PostCreationRequest;
import com.gray.bird.testConfig.TestcontainersConfig;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestcontainersConfig.class)
@Sql(scripts = {"/sql/mockaroo/roles.sql",
		 "/sql/mockaroo/users.sql",
		 "/sql/mockaroo/posts.sql",
		 "/sql/mockaroo/replies.sql"},
	executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class PostControllerIT {
	private static final String POSTS_ENDPOINT = ResourcePaths.POSTS;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockitoBean
	private JwtService jwtService;

	// use this as authentication header
	private String accessToken = "Bearer accessToken";

	@Nested
	class AuthenticatedUser {
		private static final UUID USER_ID = UUID.randomUUID();

		@BeforeEach
		void setUp() {
			Authentication authentication = new TestingAuthenticationToken(USER_ID, "password", "USER");
			Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(true);
			Mockito.when(jwtService.getAuthenticationFromAccessToken(Mockito.anyString()))
				.thenReturn(authentication);
		}

		@Nested
		class GetPost {
			@Test
			void shouldReturnPostWhenItsRequested() throws Exception {
				Long postId = 1L;

				mockMvc
					.perform(MockMvcRequestBuilders.get(POSTS_ENDPOINT + "/{postId}", postId)
							.header(HttpHeaders.AUTHORIZATION, accessToken))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(postId.toString()))
					.andExpect(
						MockMvcResultMatchers.jsonPath("$.data.type").value(ResourceType.POSTS.getType()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.attributes.createdAt").exists())
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.relationships.user.data.id").exists())
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.relationships.user.data.type")
							.value(ResourceType.USERS.getType()));
			}

			@Test
			void shouldReturnNotFoundWhenPostIsNotFound() throws Exception {
				Long postId = 99999L;

				mockMvc
					.perform(MockMvcRequestBuilders.get(POSTS_ENDPOINT + "/{postId}", postId)
							.header(HttpHeaders.AUTHORIZATION, accessToken))
					.andExpect(MockMvcResultMatchers.status().isNotFound());
			}
		}

		@Nested
		class CreatePost {
			@Test
			@Transactional
			@Rollback
			void shouldCreatePostWithValidDataAndReturnIt() throws JsonProcessingException, Exception {
				PostCreationRequest req = new PostCreationRequest("testText", null, ReplyType.EVERYONE);

				mockMvc
					.perform(MockMvcRequestBuilders.post(POSTS_ENDPOINT)
							.header(HttpHeaders.AUTHORIZATION, accessToken)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(req)))
					.andExpect(MockMvcResultMatchers.status().isCreated())
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.id").exists())
					.andExpect(
						MockMvcResultMatchers.jsonPath("$.data.type").value(ResourceType.POSTS.getType()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.attributes.text").value(req.text()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.attributes.replyType")
							.value(req.replyType().name()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.attributes.createdAt").exists())
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.relationships.user.data.type")
							.value(ResourceType.USERS.getType()));
			}

			@Test
			void shouldReturnBadRequestWithInvalidData() throws JsonProcessingException, Exception {
				PostCreationRequest req = new PostCreationRequest(null, null, ReplyType.EVERYONE);

				mockMvc
					.perform(MockMvcRequestBuilders.post(POSTS_ENDPOINT)
							.header(HttpHeaders.AUTHORIZATION, accessToken)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(req)))
					.andExpect(MockMvcResultMatchers.status().isBadRequest());
			}
		}

		@Nested
		class DeletePost {}

		@Nested
		class GetReplies {
			@Test
			void shouldReturnRepliesWhenItsRequested() throws Exception {
				Long postId = 1L;

				mockMvc
					.perform(MockMvcRequestBuilders.get(POSTS_ENDPOINT + "/{postId}/replies", postId)
							.header(HttpHeaders.AUTHORIZATION, accessToken))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.length()", Matchers.greaterThan(0)));
			}

			@Test
			void shouldReturnNotFoundWhenPostIsNotFound() throws Exception {
				Long postId = 99999L;

				mockMvc.perform(MockMvcRequestBuilders.get(POSTS_ENDPOINT + "/{postId}/replies", postId))
					.andExpect(MockMvcResultMatchers.status().isNotFound());
			}
		}

		@Nested
		class PostReply {
			@Test
			@Transactional
			@Rollback
			void shouldCreateReplyWithValidDataAndReturnIt() throws Exception {
				Long postId = 1L;
				PostCreationRequest req = new PostCreationRequest("testText", null, ReplyType.EVERYONE);

				mockMvc
					.perform(MockMvcRequestBuilders.post(POSTS_ENDPOINT + "/{postId}/replies", postId)
							.header(HttpHeaders.AUTHORIZATION, accessToken)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(req)))
					.andExpect(MockMvcResultMatchers.status().isCreated())
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.id").exists())
					.andExpect(
						MockMvcResultMatchers.jsonPath("$.data.type").value(ResourceType.POSTS.getType()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.attributes.text").value(req.text()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.relationships.user.data.id")
							.value(USER_ID.toString()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.relationships.user.data.type")
							.value(ResourceType.USERS.getType()));
			}

			@Test
			void shouldReturnBadRequestWithInvalidData() throws Exception {
				Long postId = 1L;
				PostCreationRequest req = new PostCreationRequest(null, null, ReplyType.EVERYONE);

				mockMvc
					.perform(MockMvcRequestBuilders.post(POSTS_ENDPOINT + "/{postId}/replies", postId)
							.header(HttpHeaders.AUTHORIZATION, accessToken)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(req)))
					.andExpect(MockMvcResultMatchers.status().isBadRequest());
			}
		}
	}

	@Nested
	class UnauthenticatedUser {
		@Nested
		class GetPost {
			@Test
			void shouldReturnPostWhenItsRequested() throws Exception {
				Long postId = 1L;

				mockMvc.perform(MockMvcRequestBuilders.get(POSTS_ENDPOINT + "/{postId}", postId))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(postId.toString()))
					.andExpect(
						MockMvcResultMatchers.jsonPath("$.data.type").value(ResourceType.POSTS.getType()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.attributes.createdAt").exists())
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.relationships.user.data.id").exists())
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.relationships.user.data.type")
							.value(ResourceType.USERS.getType()));
			}

			@Test
			void shouldReturnNotFoundWhenPostIsNotFound() throws Exception {
				Long postId = 99999L;

				mockMvc.perform(MockMvcRequestBuilders.get(POSTS_ENDPOINT + "/{postId}", postId))
					.andExpect(MockMvcResultMatchers.status().isNotFound());
			}
		}

		@Nested
		class CreatePost {
			@Test
			void shouldReturnUnauthorizedWhenNotAuthenticated() throws JsonProcessingException, Exception {
				PostCreationRequest req = new PostCreationRequest("testText", null, ReplyType.EVERYONE);

				mockMvc
					.perform(MockMvcRequestBuilders.post(POSTS_ENDPOINT)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(req)))
					.andExpect(MockMvcResultMatchers.status().isUnauthorized());
			}
		}

		@Nested
		class GetReplies {
			@Test
			void shouldReturnRepliesWhenItsRequested() throws Exception {
				Long postId = 1L;

				mockMvc.perform(MockMvcRequestBuilders.get(POSTS_ENDPOINT + "/{postId}/replies", postId))
					.andExpect(MockMvcResultMatchers.status().isOk());
			}

			@Test
			void shouldReturnNotFoundWhenPostIsNotFound() throws Exception {
				Long postId = 99999L;

				mockMvc.perform(MockMvcRequestBuilders.get(POSTS_ENDPOINT + "/{postId}/replies", postId))
					.andExpect(MockMvcResultMatchers.status().isNotFound());
			}
		}

		@Nested
		class PostReply {
			@Test
			void shouldReturnUnauthorizedWhenNotAuthenticated() throws JsonProcessingException, Exception {
				Long postId = 1L;
				PostCreationRequest req = new PostCreationRequest("testText", null, ReplyType.EVERYONE);

				mockMvc
					.perform(MockMvcRequestBuilders.post(POSTS_ENDPOINT + "/{postId}/replies", postId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(req)))
					.andExpect(MockMvcResultMatchers.status().isUnauthorized());
			}
		}
	}
}
