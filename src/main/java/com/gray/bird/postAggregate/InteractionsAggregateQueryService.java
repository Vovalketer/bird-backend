package com.gray.bird.postAggregate;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.gray.bird.like.LikeQueryService;
import com.gray.bird.post.PostQueryService;
import com.gray.bird.repost.RepostQueryService;

@Service
@RequiredArgsConstructor
public class InteractionsAggregateQueryService {
	private final LikeQueryService likesQueryService;
	private final RepostQueryService repostQueryService;
	private final PostQueryService postQueryService;

	public InteractionsAggregate getInteractionsById(Long id) {
		Long likesCount = likesQueryService.getLikesCountByPostId(id);
		Long repostCount = repostQueryService.getRepostCountByPostId(id);
		Long repliesCount = postQueryService.getRepliesCountByPostId(id);

		return new InteractionsAggregate(id, repliesCount, likesCount, repostCount);
	}

	public List<InteractionsAggregate> getAllInteractionsByIds(Iterable<Long> postIds) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getAllInteractionsByIds'");
	}
}
