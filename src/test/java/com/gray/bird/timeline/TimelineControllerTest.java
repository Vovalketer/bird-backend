package com.gray.bird.timeline;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import com.gray.bird.common.JsonApiResponse;
import com.gray.bird.common.PaginationMetadata;
import com.gray.bird.common.utils.JsonApiResponseFactory;
import com.gray.bird.common.utils.MetadataUtils;
import com.gray.bird.post.dto.PostResource;
import com.gray.bird.postAggregator.PostAggregate;
import com.gray.bird.postAggregator.PostAggregateResourceMapper;
import com.gray.bird.postAggregator.PostAggregatorService;
import com.gray.bird.timeline.dto.TimelineEntryDto;
import com.gray.bird.user.UserService;
import com.gray.bird.utils.TestResources;
import com.gray.bird.utils.TestUtils;

@ExtendWith(SpringExtension.class)
public class TimelineControllerTest {
	@Mock
	private TimelineService timelineService;
	@Mock
	private PostAggregatorService postAggregatorService;
	@Mock
	private UserService userService;
	@Mock
	private MetadataUtils metadataUtils;
	@Mock
	private JsonApiResponseFactory responseFactory;
	@Mock
	private PostAggregateResourceMapper postAggregateResourceMapper;
	@InjectMocks
	private TimelineController timelineController;
	private TestUtils testUtils = new TestUtils();
	private TestResources testResources = new TestResources();

	@Test
	void testGetUserTimeline() {
		String username = "testUsername";
		int pageNumber = 0;
		int pageSize = 10;

		UUID userId = UUID.randomUUID();
		Mockito.when(userService.getUserIdByUsername(username)).thenReturn(userId);

		Page<TimelineEntryDto> homeTimeline =
			new PageImpl<>(List.of(new TimelineEntryDto(userId, 100L), new TimelineEntryDto(userId, 101L)));
		Mockito.when(timelineService.getHomeTimeline(Mockito.eq(userId), Mockito.any(Pageable.class)))
			.thenReturn(homeTimeline);

		List<PostAggregate> posts = List.of(testUtils.createPostAggregateWithoutMedia(userId, 100L),
			testUtils.createPostAggregateWithoutMedia(userId, 101L));
		Mockito.when(postAggregatorService.getPosts(Mockito.anyList())).thenReturn(posts);

		List<PostResource> resources =
			List.of(testResources.createPostResource(100L, userId.toString(), null),
				testResources.createPostResource(101L, userId.toString(), null));
		int resourceCount = 0;
		Mockito.when(postAggregateResourceMapper.toResource(Mockito.any(PostAggregate.class)))
			.thenReturn(resources.get(resourceCount++));

		JsonApiResponse<List<PostResource>> response = new JsonApiResponse<>(resources);
		// type hint to avoid issues with generics, otherwise it'll demand for a List<Object> as return type
		Mockito.<JsonApiResponse<List<PostResource>>>when(responseFactory.createResponse(Mockito.anyList()))
			.thenReturn(response);

		PaginationMetadata paginationMetadata = PaginationMetadata.fromPage(homeTimeline);
		Mockito.when(metadataUtils.extractPaginationMetadata(homeTimeline)).thenReturn(paginationMetadata);

		ResponseEntity<JsonApiResponse<List<PostResource>>> userTimeline =
			timelineController.getUserTimeline(username, pageNumber, pageSize);

		Assertions.assertThat(userTimeline.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(userTimeline.getBody()).isNotNull();
		Assertions.assertThat(userTimeline.getBody().getData()).isNotNull();
		Assertions.assertThat(userTimeline.getBody().getData().size()).isEqualTo(2);
		Assertions.assertThat(userTimeline.getBody().getMetadata()).isNotNull();
	}
}
