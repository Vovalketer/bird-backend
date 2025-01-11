package com.gray.bird.postAggregate;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gray.bird.like.LikeService;
import com.gray.bird.like.dto.LikesCount;
import com.gray.bird.post.PostQueryService;
import com.gray.bird.post.dto.RepliesCount;
import com.gray.bird.postAggregate.dto.PostInteractions;
import com.gray.bird.repost.RepostService;
import com.gray.bird.repost.dto.RepostsCount;

@Service
@RequiredArgsConstructor
public class PostInteractionsService {
	private final LikeService likesQueryService;
	private final RepostService repostQueryService;
	private final PostQueryService postQueryService;

	public PostInteractions getInteractionsById(Long id) {
		Long likesCount = likesQueryService.getLikesCountByPostId(id).likesCount();
		Long repostCount = repostQueryService.getRepostCountByPostId(id).repostsCount();
		Long repliesCount = postQueryService.getRepliesCountByPostId(id).repliesCount();

		return new PostInteractions(id, repliesCount, likesCount, repostCount);
	}

	public List<InteractionsAggregate> getAllInteractionsByIds(Iterable<Long> postIds) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getAllInteractionsByIds'");
	}
}
