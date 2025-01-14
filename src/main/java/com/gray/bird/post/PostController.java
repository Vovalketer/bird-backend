package com.gray.bird.post;

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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gray.bird.common.ResourcePaths;
import com.gray.bird.common.jsonApi.ResourceCollectionAggregate;
import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceResponseFactory;
import com.gray.bird.common.jsonApi.ResourceSingleAggregate;
import com.gray.bird.common.utils.MetadataType;
import com.gray.bird.common.utils.MetadataUtils;
import com.gray.bird.post.dto.PostCreationRequest;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.postAggregator.PostAggregate;
import com.gray.bird.postAggregator.PostAggregateResourceMapper;
import com.gray.bird.postAggregator.PostAggregatorService;
import com.gray.bird.user.UserResourceMapper;
import com.gray.bird.user.UserService;
import com.gray.bird.user.dto.UserProjection;

@RestController
@RequestMapping(ResourcePaths.POSTS)
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;
	private final UserService userService;
	private final PostAggregatorService postAggregatorService;
	private final PostAggregateResourceMapper postAggregateResourceMapper;
	private final PostResourceMapper postResourceMapper;
	private final UserResourceMapper userResourceMapper;
	private final ResourceResponseFactory responseFactory;
	private final MetadataUtils metadataUtils;

	// TODO: handle protected accounts
	// TODO: validation of the type of reply

	@PostMapping
	public ResponseEntity<?> createPost(
		@RequestBody PostCreationRequest postRequest, @AuthenticationPrincipal UUID userId) {
		PostProjection post = postService.createPost(postRequest, userId);
		ResourceData resource = postResourceMapper.toResource(post);

		ResourceSingleAggregate response = responseFactory.createResponse(resource);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{postId}")
	public ResponseEntity<?> getPost(@PathVariable Long postId) {
		System.out.println("getPostMethod");
		PostAggregate postAggregate = postAggregatorService.getPost(postId);
		ResourceData postResource = postAggregateResourceMapper.toResource(postAggregate);

		UserProjection user = userService.getUserByUuid(postAggregate.post().userId());
		ResourceData userResource = userResourceMapper.toResource(user);

		ResourceSingleAggregate response = responseFactory.createResponse(postResource, userResource);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{postId}/replies")
	public ResponseEntity<?> getReplies(@PathVariable Long postId,
		@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {
		// public endpoint (unless the account is set to private)
		// user posts and reposts
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<Long> replyIds = postService.getReplyIds(postId, pageable);
		List<PostAggregate> replies = postAggregatorService.getPosts(replyIds.getContent());
		List<ResourceData> repliesResource =
			replies.stream().map(postAggregateResourceMapper::toResource).collect(Collectors.toList());

		List<UUID> userIds = replies.stream().map(p -> p.post().userId()).collect(Collectors.toList());
		List<UserProjection> users = userService.getAllUsersById(userIds);
		List<ResourceData> usersResource =
			users.stream().map(userResourceMapper::toResource).collect(Collectors.toList());

		ResourceCollectionAggregate response = responseFactory.createResponse(repliesResource, usersResource);
		response.addMetadata(
			MetadataType.PAGINATION.getValue(), metadataUtils.extractPaginationMetadata(replyIds));

		return ResponseEntity.ok(response);
	}

	@PostMapping("/{postId}/replies")
	public ResponseEntity<?> postReply(@PathVariable Long postId,
		@RequestBody PostCreationRequest postRequest, @AuthenticationPrincipal UUID userId) {
		PostProjection reply = postService.createReply(postRequest, postId, userId);
		ResourceData resource = postResourceMapper.toResource(reply);

		ResourceSingleAggregate response = responseFactory.createResponse(resource);

		return ResponseEntity.ok(response);
	}

	// TODO: latest posts on /posts, no filters
}
