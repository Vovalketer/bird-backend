package com.gray.bird.timeline;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gray.bird.common.JsonApiResponse;
import com.gray.bird.common.ResourcePaths;
import com.gray.bird.common.utils.JsonApiResponseFactory;
import com.gray.bird.common.utils.MetadataUtils;
import com.gray.bird.post.dto.PostResource;
import com.gray.bird.postAggregator.PostAggregate;
import com.gray.bird.postAggregator.PostAggregateResourceMapper;
import com.gray.bird.postAggregator.PostAggregatorService;
import com.gray.bird.timeline.dto.TimelineEntryDto;
import com.gray.bird.user.UserResourceMapper;
import com.gray.bird.user.UserService;
import com.gray.bird.user.dto.UserProjection;

@RestController
@RequestMapping(ResourcePaths.FEEDS)
@RequiredArgsConstructor
public class TimelineController {
	private final TimelineService timelineService;
	private final PostAggregatorService postAggregatorService;
	private final UserService userService;
	private final MetadataUtils metadataUtils;
	private final JsonApiResponseFactory responseFactory;
	private final PostAggregateResourceMapper postAggregateResourceMapper;
	private final UserResourceMapper userResourceMapper;

	@GetMapping("/following")
	public ResponseEntity<JsonApiResponse<List<PostResource>>> getFollowingTimeline(
		@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
		@AuthenticationPrincipal UUID userId) {
		Pageable pageable = PageRequest.of(page, size);
		Page<TimelineEntryDto> followingTimeline = timelineService.getFollowingTimeline(userId, pageable);
		List<PostAggregate> posts = postAggregatorService.getPosts(followingTimeline.getContent()
				.stream()
				.map(TimelineEntryDto::postId)
				.collect(Collectors.toList()));
		List<PostResource> resources =
			posts.stream().map(postAggregateResourceMapper::toResource).collect(Collectors.toList());
		JsonApiResponse<List<PostResource>> response = responseFactory.createResponse(resources);
		response.addMetadata("pagination", metadataUtils.extractPaginationMetadata(followingTimeline));

		List<UserProjection> users = userService.getAllUsersById(
			posts.stream().map(p -> p.post().userId()).collect(Collectors.toList()));
		response.includeAllUsers(
			users.stream().map(userResourceMapper::toResource).collect(Collectors.toList()));

		return ResponseEntity.ok(response);
	}
}
