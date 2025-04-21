package com.gray.bird.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gray.bird.common.JsonApiResponse;
import com.gray.bird.common.ResourcePaths;
import com.gray.bird.common.utils.JsonApiResponseFactory;
import com.gray.bird.common.utils.MetadataType;
import com.gray.bird.common.utils.MetadataUtils;
import com.gray.bird.media.dto.request.MediaMetadataRequest;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.post.dto.PostResource;
import com.gray.bird.post.dto.request.PostContentRequest;
import com.gray.bird.post.dto.request.PostRequest;
import com.gray.bird.post.mapper.PostRequestMapper;
import com.gray.bird.postAggregator.PostAggregate;
import com.gray.bird.postAggregator.PostAggregateResourceMapper;
import com.gray.bird.postAggregator.PostAggregatorService;
import com.gray.bird.user.UserResourceMapper;
import com.gray.bird.user.UserService;
import com.gray.bird.user.dto.UserProjection;
import com.gray.bird.user.dto.UserResource;

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
	private final PostRequestMapper postCreationRequestMapper;
	private final MetadataUtils metadataUtils;
	private final JsonApiResponseFactory responseFactory;

	// TODO: handle protected accounts
	// TODO: validation of the type of reply

	@PostMapping
	public ResponseEntity<JsonApiResponse<PostResource>> createPost(
		@RequestPart("content") @Valid PostContentRequest content,
		@RequestPart(name = "media", required = false) List<MultipartFile> files,
		@RequestPart(name = "metadata", required = false) Map<Integer, MediaMetadataRequest> metadata,
		@AuthenticationPrincipal UUID userId) {
		PostRequest request = postCreationRequestMapper.toPostCreationRequest(content, files, metadata);
		PostProjection postProjection = postService.createPost(request, userId);
		PostResource resource = postResourceMapper.toResource(postProjection);

		var response = responseFactory.createResponse(resource);

		return ResponseEntity.created(URI.create(ResourcePaths.POSTS + "/" + response.getData().getId()))
			.body(response);
	}

	@GetMapping("/{postId}")
	public ResponseEntity<JsonApiResponse<PostResource>> getPost(
		@PathVariable Long postId, @AuthenticationPrincipal UUID userId) {
		PostAggregate postAggregate = postAggregatorService.getPost(postId, userId);
		PostResource postResource = postAggregateResourceMapper.toResource(postAggregate);

		UserProjection user = userService.getUserById(postAggregate.post().userId());
		UserResource userResource = userResourceMapper.toResource(user);

		var response = responseFactory.createResponse(postResource);
		response.includeUser(userResource);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{postId}/replies")
	public ResponseEntity<JsonApiResponse<List<PostResource>>> getReplies(@PathVariable Long postId,
		@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int limit,
		@AuthenticationPrincipal UUID userId) {
		Pageable pageable = PageRequest.of(page, limit);

		// get replies
		Page<Long> replyIds = postService.getReplyIds(postId, pageable);

		List<PostAggregate> replies = postAggregatorService.getPosts(replyIds.getContent(), userId);

		List<PostResource> repliesResource =
			replies.stream().map(postAggregateResourceMapper::toResource).collect(Collectors.toList());

		// get users
		List<UUID> userIds = replies.stream().map(p -> p.post().userId()).collect(Collectors.toList());
		List<UserProjection> users = userService.getAllUsersById(userIds);
		List<UserResource> usersResource =
			users.stream().map(userResourceMapper::toResource).collect(Collectors.toList());

		JsonApiResponse<List<PostResource>> response = responseFactory.createResponse(repliesResource);
		response.includeAllUsers(usersResource);

		response.addMetadata(
			MetadataType.PAGINATION.getValue(), metadataUtils.extractPaginationMetadata(replyIds));

		return ResponseEntity.ok(response);
	}

	@PostMapping("/{postId}/replies")
	public ResponseEntity<JsonApiResponse<PostResource>> createReply(@PathVariable Long postId,
		@RequestPart("content") @Valid PostContentRequest content,
		@RequestPart(name = "media", required = false) List<MultipartFile> files,
		@RequestPart(name = "metadata", required = false) Map<Integer, MediaMetadataRequest> metadata,
		@AuthenticationPrincipal UUID userId) {
		PostRequest request = postCreationRequestMapper.toPostCreationRequest(content, files, metadata);
		PostProjection reply = postService.createReply(request, postId, userId);
		PostResource resource = postResourceMapper.toResource(reply);

		var response = responseFactory.createResponse(resource);

		return ResponseEntity.created(URI.create(ResourcePaths.POSTS + "/" + response.getData().getId()))
			.body(response);
	}

	// TODO: latest posts on /posts, no filters
}
