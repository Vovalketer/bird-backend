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

	public List<PostInteractions> getAllInteractionsByIds(Collection<Long> postIds) {
		List<LikesCount> likeCounts = likesQueryService.getLikesCountByPostIds(postIds);
		List<RepostsCount> repostCounts = repostQueryService.getRepostCountByPostIds(postIds);
		List<RepliesCount> replyCounts = postQueryService.getRepliesCountByPostIds(postIds);

		// not the prettiest solution, but it works
		// preferable over iterating/filtering through each list matching the postId
		Map<Long, Long> likeCountsMap =
			likeCounts.stream().collect(Collectors.toMap(LikesCount::postId, LikesCount::likesCount));
		Map<Long, Long> repostCountsMap =
			repostCounts.stream().collect(Collectors.toMap(RepostsCount::postId, RepostsCount::repostsCount));
		Map<Long, Long> replyCountsMap =
			replyCounts.stream().collect(Collectors.toMap(RepliesCount::postId, RepliesCount::repliesCount));

		List<PostInteractions> collect = postIds.stream()
											 .map(postId
												 -> new PostInteractions(postId,
													 replyCountsMap.getOrDefault(postId, 0L),
													 likeCountsMap.getOrDefault(postId, 0L),
													 repostCountsMap.getOrDefault(postId, 0L)))
											 .collect(Collectors.toList());

		return collect;
	}
}
