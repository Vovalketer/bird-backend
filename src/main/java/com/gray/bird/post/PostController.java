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

import com.gray.bird.common.ResourcePaths;
import com.gray.bird.common.jsonApi.ResourceCollectionAggregate;
import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceSingleAggregate;
import com.gray.bird.common.utils.MetadataType;
import com.gray.bird.common.utils.MetadataUtils;
import com.gray.bird.post.dto.PostDto;
import com.gray.bird.post.dto.PostRequest;
import com.gray.bird.postAggregate.PostAggregate;
import com.gray.bird.postAggregate.PostAggregateQueryService;
import com.gray.bird.postAggregate.PostResourceConverter;
import com.gray.bird.user.UserQueryService;
import com.gray.bird.user.UserResourceConverter;
import com.gray.bird.user.dto.UserProjection;

@RestController
@RequestMapping(ResourcePaths.POSTS)
@RequiredArgsConstructor
public class PostController {
	private final PostCommandService postManagerService;
	private final PostQueryService postQueryService;
	private final UserQueryService userQueryService;
	private final PostAggregateQueryService postAggregateService;
	private final PostResourceConverter postResourceConverter;
	private final UserResourceConverter userResourceConverter;
	private final MetadataUtils metadataUtils;

	// TODO: handle protected accounts
	// TODO: validation of the type of reply

	@PostMapping
	public ResponseEntity<?> createPost(@RequestBody PostRequest postRequest, HttpServletRequest request) {
		PostDto post = postManagerService.createPost(postRequest);
		return ResponseEntity.ok(post);
	}

	@GetMapping("/{postId}")
	public ResponseEntity<?> getPost(@PathVariable Long postId) {
		System.out.println("getPostMethod");
		PostAggregate postAggregate = postAggregateService.getPost(postId);

		ResourceSingleAggregate aggregate = postResourceConverter.toAggregate(postAggregate);

		UserProjection user = userQueryService.getUserById(postAggregate.post().userId());

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
		Page<Long> replyIds = postQueryService.getReplyIds(postId, pageable);
		List<PostAggregate> replies = postAggregateService.getPosts(replyIds);
		List<UserProjection> users = userQueryService.getUsersFromPosts(replies);
		ResourceCollectionAggregate aggregate = postResourceConverter.toAggregate(replies);
		aggregate.includeAllResources(userResourceConverter.toResource(users));
		aggregate.addMetadata(
			MetadataType.PAGINATION.getValue(), metadataUtils.extractPaginationMetadata(replyIds));
		return ResponseEntity.ok(aggregate);
	}

	@PostMapping("/{postId}/replies")
	public ResponseEntity<PostDto> postReply(
		@PathVariable Long postId, @RequestBody PostRequest postRequest, HttpServletRequest request) {
		PostDto reply = postManagerService.createReply(postRequest, postId);
		return ResponseEntity.ok(reply);
	}

	// TODO: latest posts on /posts, no filters
}
