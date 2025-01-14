package com.gray.bird.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gray.bird.auth.AuthService;
import com.gray.bird.common.ResourcePaths;
import com.gray.bird.common.jsonApi.ResourceCollectionAggregate;
import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceResponseFactory;
import com.gray.bird.common.jsonApi.ResourceSingleAggregate;
import com.gray.bird.post.PostService;
import com.gray.bird.postAggregator.PostAggregate;
import com.gray.bird.postAggregator.PostAggregateResourceMapper;
import com.gray.bird.postAggregator.PostAggregatorService;
import com.gray.bird.user.dto.UserCreationRequest;
import com.gray.bird.user.dto.UserProjection;
import com.gray.bird.user.follow.FollowService;

@RestController
@RequestMapping(path = ResourcePaths.USERS)
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final PostAggregatorService postAggregatorService;
	private final PostService postService;
	private final FollowService followService;
	private final AuthService authService;
	private final PostAggregateResourceMapper postAggregateResourceMapper;
	private final UserResourceMapper userResourceMapper;
	private final ResourceResponseFactory responseFactory;

	@PostMapping("/register")
	public ResponseEntity<?> register(
		@RequestBody @Valid UserCreationRequest data, HttpServletRequest request) {
		UserProjection user = userService.createUser(data);
		ResourceData resource = userResourceMapper.toResource(user);

		ResourceSingleAggregate response = responseFactory.createResponse(resource);
		response.addMetadata("message", "Account created. Check your email to enable it");

		return ResponseEntity.created(getUri(user.username())).body(response);
	}

	@GetMapping("/{username}")
	public ResponseEntity<?> getUserProfile(@PathVariable String username, HttpServletRequest request) {
		UserProjection userProfile = userService.getUserByUsername(username);
		ResourceData resource = userResourceMapper.toResource(userProfile);
		ResourceSingleAggregate response = responseFactory.createResponse(resource);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{username}/posts")
	public ResponseEntity<?> getUserPosts(@PathVariable String username,
		@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {
		// just the user posts and its replies, no reposts
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		UUID userId = userService.getUserIdByUsername(username);
		Page<Long> postIds = postService.getPostIdsByUserId(userId, pageable);
		List<ResourceData> resources = postAggregatorService.getPosts(postIds.getContent())
										   .stream()
										   .map(postAggregateResourceMapper::toResource)
										   .collect(Collectors.toList());
		ResourceCollectionAggregate response = responseFactory.createResponse(resources);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{username}/timelines/following")
	public ResponseEntity<?> getFollowingTimeline(@RequestParam String username) {
		// following, AUTH REQUIRED
		return null;
	}

	@GetMapping("/{username}/timelines/home")
	public ResponseEntity<?> getUserTimeline(@PathVariable String username,
		@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {
		// public endpoint (unless the account is set to private)
		// user posts and reposts
		return ResponseEntity.ok(null);
	}

	@PostMapping("/{username}/following")
	public ResponseEntity<?> postMethodName(
		@PathVariable String username, @AuthenticationPrincipal UUID userId) {
		followService.followUser(userId, username);
		return ResponseEntity.ok(null);
	}

	// TODO: consider making an URI factory/provider
	private URI getUri(String username) {
		return URI.create(ResourcePaths.USERS + "/" + username);
	}
}
