package com.gray.bird.repost;

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
@RequestMapping(ResourcePaths.POSTS_POSTID_REPOSTS)
public class RepostController {
	private final RepostService repostService;

	@GetMapping
	public ResponseEntity<?> getRepostedByUsers(@PathVariable Long postId,
		@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {
		return null;
	}

	@PostMapping
	public ResponseEntity<?> repostPost(@PathVariable Long postId, @AuthenticationPrincipal UUID userId) {
		repostService.repost(postId, userId);

		return ResponseEntity.ok(null);
	}

	@DeleteMapping
	public ResponseEntity<?> unrepostPost(@PathVariable Long postId, @AuthenticationPrincipal UUID userId) {
		repostService.unrepost(postId, userId);

		return ResponseEntity.ok(null);
	}
}
