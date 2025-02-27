package com.gray.bird.repost.dto;

import java.util.Optional;

public record RepostSummary(
	Long postId, long repostsCount, Optional<RepostUserInteractions> userInteractions) {
	public RepostSummary(Long postId, long repostsCount, RepostUserInteractions userInteractions) {
		this(postId, repostsCount, Optional.of(userInteractions));
	}
	public RepostSummary(Long postId, long repostsCount) {
		this(postId, repostsCount, Optional.ofNullable(null));
	}
}
