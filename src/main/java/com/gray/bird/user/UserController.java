package com.gray.bird.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gray.bird.common.JsonApiResponse;
import com.gray.bird.common.PaginationMetadata;
import com.gray.bird.common.ResourcePaths;
import com.gray.bird.common.utils.JsonApiResponseFactory;
import com.gray.bird.common.utils.MetadataUtils;
import com.gray.bird.like.LikeService;
import com.gray.bird.post.PostService;
import com.gray.bird.post.dto.PostResource;
import com.gray.bird.postAggregator.PostAggregate;
import com.gray.bird.postAggregator.PostAggregateResourceMapper;
import com.gray.bird.postAggregator.PostAggregatorService;
import com.gray.bird.timeline.TimelineService;
import com.gray.bird.timeline.dto.TimelineEntryDto;
import com.gray.bird.user.dto.UserCreationRequest;
import com.gray.bird.user.dto.UserProjection;
import com.gray.bird.user.dto.UserResource;
import com.gray.bird.user.follow.FollowService;
import com.gray.bird.user.follow.dto.FollowCounts;

@RestController
@RequestMapping(path = ResourcePaths.USERS)
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final PostAggregatorService postAggregatorService;
	private final PostService postService;
	private final TimelineService timelineService;
	private final FollowService followService;
	private final LikeService likeService;
	private final PostAggregateResourceMapper postAggregateResourceMapper;
	private final UserResourceMapper userResourceMapper;
	private final JsonApiResponseFactory responseFactory;
	private final MetadataUtils metadataUtils;

	@PostMapping("/register")
	public ResponseEntity<JsonApiResponse<UserResource>> register(
		@RequestBody @Valid UserCreationRequest data) {
		UserProjection user = userService.createUser(data);
		UserResource resource = userResourceMapper.toResource(user);

		var response = responseFactory.createResponse(resource);
		response.addMetadata("message", "Account created. Check your email to enable it");

		return ResponseEntity.created(getUri(user.username())).body(response);
	}

	@GetMapping("/{username}")
	public ResponseEntity<JsonApiResponse<UserResource>> getUserProfile(
		@PathVariable String username, @AuthenticationPrincipal UUID userId) {
		UserProjection userProfile = userService.getUserByUsername(username);
		UserResource resource = userResourceMapper.toResource(userProfile);
		FollowCounts followCounts = followService.getFollowCounts(userProfile.uuid());
		resource.addMetadata("followCounts", followCounts);
		var response = responseFactory.createResponse(resource);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{username}/posts")
	public ResponseEntity<JsonApiResponse<List<PostResource>>> getUserPosts(@PathVariable String username,
		@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int limit,
		@AuthenticationPrincipal UUID authUserId) {
		// just the user posts and its replies, no reposts
		Pageable pageable = PageRequest.of(page, limit);
		UUID userId = userService.getUserIdByUsername(username);
		Page<Long> postIds = postService.getPostIdsByUserId(userId, pageable);
		List<PostResource> resources = postAggregatorService.getPosts(postIds.getContent(), authUserId)
										   .stream()
										   .map(postAggregateResourceMapper::toResource)
										   .collect(Collectors.toList());
		var response = responseFactory.createResponse(resources);
		UserProjection user = userService.getUserByUsername(username);
		UserResource userResource = userResourceMapper.toResource(user);
		response.includeUser(userResource);
		PaginationMetadata paginationMetadata = metadataUtils.extractPaginationMetadata(postIds);
		response.addMetadata("pagination", paginationMetadata);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{username}/timeline")
	public ResponseEntity<JsonApiResponse<List<PostResource>>> getUserTimeline(@PathVariable String username,
		@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int limit,
		@AuthenticationPrincipal UUID authUserId) {
		Pageable pageable = PageRequest.of(page, limit);
		UUID userId = userService.getUserIdByUsername(username);
		// to define whether the response is a dto or a raw Long
		Page<TimelineEntryDto> homeTimeline = timelineService.getHomeTimeline(userId, pageable);
		List<PostAggregate> posts = postAggregatorService.getPosts(
			homeTimeline.getContent().stream().map(TimelineEntryDto::postId).collect(Collectors.toList()),
			authUserId);
		List<PostResource> resources =
			posts.stream().map(postAggregateResourceMapper::toResource).collect(Collectors.toList());
		JsonApiResponse<List<PostResource>> response = responseFactory.createResponse(resources);
		PaginationMetadata paginationMetadata = metadataUtils.extractPaginationMetadata(homeTimeline);
		response.addMetadata("pagination", paginationMetadata);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/{username}/following")
	public ResponseEntity<Void> follow(@PathVariable String username, @AuthenticationPrincipal UUID userId) {
		followService.followUser(userId, username);
		return ResponseEntity.ok(null);
	}

	@DeleteMapping("/{username}/following")
	public ResponseEntity<Void> unfollow(
		@PathVariable String username, @AuthenticationPrincipal UUID userId) {
		followService.unfollowUser(userId, username);
		return ResponseEntity.ok(null);
	}

	@GetMapping("/{username}/likes")
	public ResponseEntity<JsonApiResponse<List<PostResource>>> getUserLikes(@PathVariable String username,
		@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int limit,
		@AuthenticationPrincipal UUID authUserId) {
		Pageable pageable = PageRequest.of(page, limit);
		UUID userId = userService.getUserIdByUsername(username);
		Page<Long> postIds = likeService.getLikedPostIdsByUserId(userId, pageable);
		List<PostResource> resources = postAggregatorService.getPosts(postIds.getContent(), authUserId)
										   .stream()
										   .map(postAggregateResourceMapper::toResource)
										   .collect(Collectors.toList());
		var response = responseFactory.createResponse(resources);
		List<UUID> userIds = resources.stream()
								 .map(p -> p.getRelationships().getUser().getData().getId())
								 .collect(Collectors.toList());
		List<UserResource> users = userService.getAllUsersById(userIds)
									   .stream()
									   .map(userResourceMapper::toResource)
									   .collect(Collectors.toList());
		response.includeAllUsers(users);
		PaginationMetadata paginationMetadata = metadataUtils.extractPaginationMetadata(postIds);
		response.addMetadata("pagination", paginationMetadata);

		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<JsonApiResponse<UserResource>> getCurrentUser(
		@AuthenticationPrincipal UUID userId) {
		UserProjection user = userService.getUserById(userId);
		JsonApiResponse<UserResource> response =
			responseFactory.createResponse(userResourceMapper.toResource(user));

		return ResponseEntity.ok(response);
	}

	// TODO: consider making an URI factory/provider
	private URI getUri(String username) {
		return URI.create(ResourcePaths.USERS + "/" + username);
	}
}
