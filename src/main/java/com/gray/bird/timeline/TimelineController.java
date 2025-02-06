package com.gray.bird.timeline;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gray.bird.common.JsonApiResponse;
import com.gray.bird.common.PaginationMetadata;
import com.gray.bird.common.ResourcePaths;
import com.gray.bird.common.utils.JsonApiResponseFactory;
import com.gray.bird.common.utils.MetadataUtils;
import com.gray.bird.post.dto.PostResource;
import com.gray.bird.postAggregator.PostAggregate;
import com.gray.bird.postAggregator.PostAggregateResourceMapper;
import com.gray.bird.postAggregator.PostAggregatorService;
import com.gray.bird.timeline.dto.TimelineEntryDto;
import com.gray.bird.user.UserService;

@RestController
@RequestMapping(ResourcePaths.USERS_USERNAME_TIMELINES)
@RequiredArgsConstructor
public class TimelineController {
	private TimelineService timelineService;
	private PostAggregatorService postAggregatorService;
	private UserService userService;
	private MetadataUtils metadataUtils;
	private JsonApiResponseFactory responseFactory;
	private PostAggregateResourceMapper postAggregateResourceMapper;

	@GetMapping("/following")
	public ResponseEntity<?> getFollowingTimeline(@RequestParam String username) {
		// following, AUTH REQUIRED
		return null;
	}

	@GetMapping("/home")
	public ResponseEntity<JsonApiResponse<List<PostResource>>> getUserTimeline(@PathVariable String username,
		@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		UUID userId = userService.getUserIdByUsername(username);
		// to define whether the response is a dto or a raw Long
		Page<TimelineEntryDto> homeTimeline = timelineService.getHomeTimeline(userId, pageable);
		List<PostAggregate> posts = postAggregatorService.getPosts(
			homeTimeline.getContent().stream().map(TimelineEntryDto::postId).collect(Collectors.toList()));
		List<PostResource> resources =
			posts.stream().map(postAggregateResourceMapper::toResource).collect(Collectors.toList());

		JsonApiResponse<List<PostResource>> response = responseFactory.createResponse(resources);
		PaginationMetadata paginationMetadata = metadataUtils.extractPaginationMetadata(homeTimeline);
		response.addMetadata("pagination", paginationMetadata);
		return ResponseEntity.ok(response);
	}
}
