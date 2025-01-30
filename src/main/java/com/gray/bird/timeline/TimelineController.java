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

import com.gray.bird.common.PaginationMetadata;
import com.gray.bird.common.ResourcePaths;
import com.gray.bird.common.jsonApi.ResourceCollectionAggregate;
import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceResponseFactory;
import com.gray.bird.common.utils.MetadataUtils;
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
	private ResourceResponseFactory responseFactory;
	private MetadataUtils metadataUtils;
	private PostAggregateResourceMapper postAggregateResourceMapper;

	@GetMapping("/following")
	public ResponseEntity<?> getFollowingTimeline(@RequestParam String username) {
		// following, AUTH REQUIRED
		return null;
	}

	@GetMapping("/home")
	public ResponseEntity<ResourceCollectionAggregate> getUserTimeline(@PathVariable String username,
		@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		UUID userId = userService.getUserIdByUsername(username);
		// to define whether the response is a dto or a raw Long
		Page<TimelineEntryDto> homeTimeline = timelineService.getHomeTimeline(userId, pageable);
		List<PostAggregate> posts = postAggregatorService.getPosts(
			homeTimeline.getContent().stream().map(TimelineEntryDto::postId).collect(Collectors.toList()));
		List<ResourceData> resources =
			posts.stream().map(postAggregateResourceMapper::toResource).collect(Collectors.toList());

		ResourceCollectionAggregate response = responseFactory.createResponse(resources);
		PaginationMetadata paginationMetadata = metadataUtils.extractPaginationMetadata(homeTimeline);
		response.addMetadata("pagination", paginationMetadata);
		return ResponseEntity.ok(response);
	}
}
