package com.gray.bird.postAggregator;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gray.bird.like.LikeService;
import com.gray.bird.like.dto.LikeSummary;
import com.gray.bird.like.dto.LikesCount;
import com.gray.bird.post.PostService;
import com.gray.bird.post.dto.RepliesCount;
import com.gray.bird.postAggregator.dto.PostInteractions;
import com.gray.bird.repost.RepostService;
import com.gray.bird.repost.dto.RepostSummary;
import com.gray.bird.repost.dto.RepostsCount;

@Service
@RequiredArgsConstructor
public class PostInteractionsService {
	private final LikeService likesService;
	private final RepostService repostService;
	private final PostService postService;

	public PostInteractions getInteractionsById(Long id) {
		Long likesCount = likesService.getLikeSummary(id).likesCount();
		Long repostCount = repostService.getRepostSummary(id).repostsCount();
		Long repliesCount = postService.getRepliesCountByPostId(id).repliesCount();

		return new PostInteractions(id, repliesCount, likesCount, repostCount);
	}

	public List<PostInteractions> getAllInteractionsByIds(Collection<Long> postIds) {
		List<LikesCount> likeCounts = likesService.getLikesCountByPostIds(postIds);
		List<RepostsCount> repostCounts = repostService.getRepostCountByPostIds(postIds);
		List<RepliesCount> replyCounts = postService.getRepliesCountByPostIds(postIds);

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

	public PostInteractions getInteractionsById(Long id, UUID userId) {
		RepliesCount repliesCount = postService.getRepliesCountByPostId(id);
		LikeSummary likeSummary = likesService.getLikeSummary(userId, id);
		RepostSummary repostSummary = repostService.getRepostSummary(userId, id);
		System.out.println("LikeSummary: " + likeSummary);
		return new PostInteractions(id,
			repliesCount.repliesCount(),
			likeSummary.likesCount(),
			repostSummary.repostsCount(),
			likeSummary.isLiked(),
			repostSummary.isReposted(),
			likeSummary.createdAt(),
			repostSummary.createdAt());
	}

	public List<PostInteractions> getAllInteractionsByIds(Collection<Long> postIds, UUID userId) {
		List<LikeSummary> likes = likesService.getLikeSummaryByPostIds(userId, postIds);
		List<RepostSummary> reposts = repostService.getRepostSummaryByPostIds(userId, postIds);
		List<RepliesCount> replies = postService.getRepliesCountByPostIds(postIds);

		Map<Long, LikeSummary> likesMap = likes.stream().collect(Collectors.toMap(l -> l.postId(), l -> l));
		Map<Long, RepostSummary> repostsMap =
			reposts.stream().collect(Collectors.toMap(r -> r.postId(), r -> r));
		Map<Long, Long> repliesMap =
			replies.stream().collect(Collectors.toMap(r -> r.postId(), r -> r.repliesCount()));

		List<PostInteractions> collect = postIds.stream()
											 .map(postId
												 -> new PostInteractions(postId,
													 repliesMap.get(postId),
													 likesMap.get(postId).likesCount(),
													 repostsMap.get(postId).repostsCount(),
													 likesMap.get(postId).isLiked(),
													 repostsMap.get(postId).isReposted(),
													 likesMap.get(postId).createdAt(),
													 repostsMap.get(postId).createdAt()))
											 .collect(Collectors.toList());
		return collect;
	}
}
