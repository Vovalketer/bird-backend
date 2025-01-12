package com.gray.bird.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gray.bird.auth.AuthService;
import com.gray.bird.common.ResourcePaths;
import com.gray.bird.common.jsonApi.ResourceCollectionAggregate;
import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceDataImpl;
import com.gray.bird.common.jsonApi.ResourceSingleAggregate;
import com.gray.bird.common.jsonApi.ResourceSingleAggregateImpl;
import com.gray.bird.exception.GlobalExceptionHandler;
import com.gray.bird.post.PostService;
import com.gray.bird.postAggregator.PostAggregate;
import com.gray.bird.postAggregator.PostAggregatorService;
import com.gray.bird.postAggregator.PostResourceConverter;
import com.gray.bird.user.dto.UserCreationRequest;
import com.gray.bird.user.dto.UserProjection;
import com.gray.bird.user.follow.FollowService;
import com.gray.bird.utils.TestResources;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class})
public class UserControllerTest {
	private final String USERS_ENDPOINT = ResourcePaths.USERS;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	private TestResources testResources = new TestResources();

	@MockitoBean
	private UserService userService;
	@MockitoBean
	private PostAggregatorService postAggregatorService;
	@MockitoBean
	private PostService postService;
	@MockitoBean
	private FollowService followService;
	@MockitoBean
	private AuthService authService;
	@MockitoBean
	private UserResourceConverter userResourceConverter;
	@MockitoBean
	private PostResourceConverter postResourceConverter;

	@Test
	void testGetUserPosts() throws Exception {
		// Mock ResourceCollectionAggregate
		ResourceCollectionAggregate aggregate = testResources.createPostCollectionAggregate(5);

		// Mock userQueryService
		Mockito.when(userService.getUserIdByUsername(Mockito.anyString())).thenReturn(UUID.randomUUID());

		// Mock postQueryService
		@SuppressWarnings("unchecked")
		Page<Long> postIds = Mockito.mock(Page.class);
		Mockito.when(postService.getPostIdsByUserId(Mockito.any(UUID.class), Mockito.any(Pageable.class)))
			.thenReturn(postIds);

		// Mock postAggregateQueryService
		List<PostAggregate> posts =
			List.of(Mockito.mock(PostAggregate.class), Mockito.mock(PostAggregate.class));
		Mockito.when(postAggregatorService.getPosts(Mockito.anyCollection())).thenReturn(posts);

		// Mock postResourceConverter
		Mockito.when(postResourceConverter.toAggregate(Mockito.anyList())).thenReturn(aggregate);

		mockMvc
			.perform(MockMvcRequestBuilders.get(USERS_ENDPOINT + "/{username}/posts", "testUser")
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.size()").value(5))
			.andExpect(
				MockMvcResultMatchers.jsonPath("$.data[0].id").value(aggregate.getData().get(0).getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].type").value("post"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].attributes.text")
					.value(aggregate.getData().get(0).getAttribute("text")))
			.andExpect(
				MockMvcResultMatchers.jsonPath("$.data[1].id").value(aggregate.getData().get(1).getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data[1].type").value("post"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data[1].attributes.text")
					.value(aggregate.getData().get(1).getAttribute("text")));
	}

	@Test
	void registerValidData() throws Exception {
		UserCreationRequest data =
			new UserCreationRequest("username", "some@email.com", "securepassword", "handle");
		UserProjection user = Mockito.mock(UserProjection.class);
		// must use an implementation instead of the interface or else it'll throw an exception
		ResourceSingleAggregateImpl aggregate = Mockito.mock(ResourceSingleAggregateImpl.class);

		Mockito.when(userService.createUser(data)).thenReturn(user);
		Mockito.when(userResourceConverter.toAggregate(Mockito.any(UserProjection.class)))
			.thenReturn(aggregate);
		Mockito.doNothing().when(aggregate).addMetadata(Mockito.anyString(), Mockito.anyString());

		mockMvc
			.perform(MockMvcRequestBuilders.post(USERS_ENDPOINT + "/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(data)))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void registerInvalidData() throws Exception {
		UserCreationRequest data = new UserCreationRequest("username", "someemail.com", "secu", "handle");
		mockMvc
			.perform(MockMvcRequestBuilders.post(USERS_ENDPOINT + "/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(data)))
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(
				MockMvcResultMatchers.jsonPath("$.message", Matchers.containsString("Validation failed")))
			.andExpect(MockMvcResultMatchers.jsonPath("$.errors.email").exists())
			.andExpect(MockMvcResultMatchers.jsonPath("$.errors.password").exists());
	}
}
