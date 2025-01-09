package com.gray.bird.like;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import com.gray.bird.common.ResourcePaths;

@RestController
@RequiredArgsConstructor
@RequestMapping(ResourcePaths.LIKES)
public class LikeController {
	private final LikeCommandService likeCommandService;
	private final LikeQueryService likesQueryService;

	@GetMapping
	public ResponseEntity<?> getLikingUsers(@PathVariable Long postId,
		@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		return ResponseEntity.ok(null);
	}

	@PostMapping
	public ResponseEntity<?> likePost(@PathVariable Long postId, @AuthenticationPrincipal UUID userId) {
		// TODO: make sure the.userId is the same as the one in the JWT
		likeCommandService.likePost(userId, postId);

		return ResponseEntity.ok(null);
	}

	@DeleteMapping
	public ResponseEntity<?> unlikePost(@PathVariable Long postId, @AuthenticationPrincipal UUID userId) {
		likeCommandService.unlikePost(userId, postId);

		return ResponseEntity.ok(null);
	}
}
