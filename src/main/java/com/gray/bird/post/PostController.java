package com.gray.bird.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gray.bird.common.ResourcePaths;
import com.gray.bird.common.jsonApi.ResourceCollectionAggregate;
import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceSingleAggregate;
import com.gray.bird.common.utils.MetadataType;
import com.gray.bird.common.utils.MetadataUtils;
import com.gray.bird.post.dto.PostCreationRequest;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.postAggregator.PostAggregate;
import com.gray.bird.postAggregator.PostAggregatorService;
import com.gray.bird.postAggregator.PostResourceConverter;
import com.gray.bird.user.UserResourceConverter;
import com.gray.bird.user.UserService;
import com.gray.bird.user.dto.UserProjection;

@RestController
@RequestMapping(ResourcePaths.POSTS)
@RequiredArgsConstructor
public class PostController {
	private final PostService postManagerService;
	private final PostService postService;
	private final UserService userService;
	private final PostAggregatorService postAggregatorService;
	private final PostResourceConverter postResourceConverter;
	private final UserResourceConverter userResourceConverter;
	private final MetadataUtils metadataUtils;

	// TODO: handle protected accounts
	// TODO: validation of the type of reply

	@PostMapping
	public ResponseEntity<?> createPost(
		@RequestBody PostCreationRequest postRequest, HttpServletRequest request) {
		PostProjection post = postManagerService.createPost(postRequest);
		return ResponseEntity.ok(post);
	}

	@GetMapping("/{postId}")
	public ResponseEntity<?> getPost(@PathVariable Long postId) {
		System.out.println("getPostMethod");
		PostAggregate postAggregate = postAggregatorService.getPost(postId);

		ResourceSingleAggregate aggregate = postResourceConverter.toAggregate(postAggregate);

		UserProjection user = userService.getUserByUuid(postAggregate.post().userId());

		ResourceData userResource = userResourceConverter.toResource(user);

		// TODO: add the relationship on this step
		aggregate.includeResource(userResource);

		return ResponseEntity.ok(aggregate);
	}

	@GetMapping("/{postId}/replies")
	public ResponseEntity<?> getReplies(@PathVariable Long postId,
		@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {
		// public endpoint (unless the account is set to private)
		// user posts and reposts
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<Long> replyIds = postService.getReplyIds(postId, pageable);
		List<PostAggregate> replies = postAggregatorService.getPosts(replyIds.getContent());
		List<UUID> userIds = replies.stream().map(p -> p.post().userId()).collect(Collectors.toList());
		List<UserProjection> users = userService.getAllUsersById(userIds);
		ResourceCollectionAggregate aggregate = postResourceConverter.toAggregate(replies);
		aggregate.includeAllResources(userResourceConverter.toResource(users));
		aggregate.addMetadata(
			MetadataType.PAGINATION.getValue(), metadataUtils.extractPaginationMetadata(replyIds));
		return ResponseEntity.ok(aggregate);
	}

	@PostMapping("/{postId}/replies")
	public ResponseEntity<?> postReply(
		@PathVariable Long postId, @RequestBody PostCreationRequest postRequest, HttpServletRequest request) {
		PostProjection reply = postManagerService.createReply(postRequest, postId);
		return ResponseEntity.ok(reply);
	}

	// TODO: latest posts on /posts, no filters
}
