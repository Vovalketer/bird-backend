package com.gray.bird.repost;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.gray.bird.common.ResourcePaths;

@RestController
@RequiredArgsConstructor
@RequestMapping(ResourcePaths.RETWEETS)
public class RepostController {
	private final RepostManager repostManager;
	private final RepostQueryService repostQueryService;

	@GetMapping
	public ResponseEntity<?> getRepostedByUsers(@PathVariable Long postId,
		@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {
		return null;
	}

	@PostMapping
	public ResponseEntity<?> repostPost(@PathVariable Long postId) {
		repostManager.repost(postId);

		return ResponseEntity.ok(null);
	}

	@DeleteMapping
	public ResponseEntity<?> unrepostPost(@PathVariable Long postId) {
		repostManager.unrepost(postId);

		return ResponseEntity.ok(null);
	}
}
