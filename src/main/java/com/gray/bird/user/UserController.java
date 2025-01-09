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

import com.gray.bird.auth.AuthService;
import com.gray.bird.common.ResourcePaths;
import com.gray.bird.common.jsonApi.ResourceCollectionAggregate;
import com.gray.bird.common.jsonApi.ResourceSingleAggregate;
import com.gray.bird.post.PostQueryService;
import com.gray.bird.postAggregate.PostAggregate;
import com.gray.bird.postAggregate.PostAggregateQueryService;
import com.gray.bird.postAggregate.PostResourceConverter;
import com.gray.bird.user.dto.RegisterRequest;
import com.gray.bird.user.dto.UserProjection;
import com.gray.bird.user.follow.FollowService;

@RestController
@RequestMapping(path = ResourcePaths.USERS)
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final PostAggregateQueryService postAggregateQueryService;
	private final PostQueryService postQueryService;
	private final FollowService followService;
	private final AuthService authService;
	private final UserQueryService userQueryService;
	private final UserResourceConverter userResourceConverter;
	private final PostResourceConverter postResourceConverter;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest data, HttpServletRequest request) {
		UserProjection user = userService.createUser(data);
		ResourceSingleAggregate aggregate = userResourceConverter.toAggregate(user);
		aggregate.addMetadata("message", "Account created. Check your email to enable it");
		return ResponseEntity.created(getUri(user.username())).body(aggregate);
	}

	@GetMapping("/{username}")
	public ResponseEntity<?> getUserProfile(@PathVariable String username, HttpServletRequest request) {
		UserProjection userProfile = userQueryService.getUserByUsername(username);
		ResourceSingleAggregate aggregate = userResourceConverter.toAggregate(userProfile);
		return ResponseEntity.ok(aggregate);
	}

	@GetMapping("/{username}/posts")
	public ResponseEntity<?> getUserPosts(@PathVariable String username,
		@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {
		// just the user posts and its replies, no reposts
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Long userId = userQueryService.getUserIdByUsername(username);
		Page<Long> postIds = postQueryService.getPostIdsByUserId(userId, pageable);
		List<PostAggregate> posts = postAggregateQueryService.getPosts(postIds.getContent());
		ResourceCollectionAggregate aggregate = postResourceConverter.toAggregate(posts);

		return ResponseEntity.ok(aggregate);
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
