package com.gray.bird.postAggregate;

public record InteractionsAggregate(
	Long postId, Long repliesCount, Long likesCount, Long repostsCount) {
}
