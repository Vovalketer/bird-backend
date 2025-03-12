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
import com.gray.bird.post.PostService;
import com.gray.bird.post.dto.RepliesCount;
import com.gray.bird.postAggregator.dto.PostEngagement;
import com.gray.bird.postAggregator.dto.PostMetrics;
import com.gray.bird.postAggregator.dto.UserPostInteractions;
import com.gray.bird.repost.RepostService;
import com.gray.bird.repost.dto.RepostSummary;

@Service
@RequiredArgsConstructor
public class PostInteractionsService {
	private final LikeService likesService;
	private final RepostService repostService;
	private final PostService postService;

	public PostEngagement getInteractionsById(Long id, UUID userId) {
		RepliesCount repliesCount = postService.getRepliesCountByPostId(id);
		LikeSummary likeSummary = likesService.getLikeSummary(id, userId);
		var metrics = new PostMetrics(
			repliesCount.repliesCount(), likeSummary.likesCount(), repostSummary.repostsCount());
		RepostSummary repostSummary = repostService.getRepostSummary(id, userId);
		UserPostInteractions userInteractions = handleUserInteractions(likeSummary, repostSummary);
		return new PostEngagement(id, metrics, userInteractions);
	}

	public List<PostEngagement> getAllInteractionsByIds(Collection<Long> postIds, UUID userId) {
		List<LikeSummary> likes = likesService.getLikeSummaryByPostIds(postIds, userId);
		List<RepostSummary> reposts = repostService.getRepostSummaryByPostIds(postIds, userId);
		List<RepliesCount> replies = postService.getRepliesCountByPostIds(postIds);

		Map<Long, LikeSummary> likesMap = likes.stream().collect(Collectors.toMap(l -> l.postId(), l -> l));
		Map<Long, RepostSummary> repostsMap =
			reposts.stream().collect(Collectors.toMap(r -> r.postId(), r -> r));
		Map<Long, RepliesCount> repliesMap =
			replies.stream().collect(Collectors.toMap(r -> r.postId(), r -> r));

		List<PostEngagement> collect =
			postIds.stream()
				.map(postId
					-> new PostEngagement(postId,
						handleMetrics(repliesMap.getOrDefault(postId, new RepliesCount(postId, 0L)),
							likesMap.getOrDefault(postId, new LikeSummary(postId, 0L)),
							repostsMap.getOrDefault(postId, new RepostSummary(postId, 0L))),
						handleUserInteractions(likesMap.get(postId), repostsMap.get(postId))))
				.collect(Collectors.toList());
		return collect;
	}

	private PostMetrics handleMetrics(
		RepliesCount repliesCount, LikeSummary likeSummary, RepostSummary repostSummary) {
		return new PostMetrics(
			repliesCount.repliesCount(), likeSummary.likesCount(), repostSummary.repostsCount());
	}

	private UserPostInteractions handleUserInteractions(
		LikeSummary likeSummary, RepostSummary repostSummary) {
		UserPostInteractions userInteractions = null;
		if (likeSummary.userInteractions().isPresent() && repostSummary.userInteractions().isPresent()) {
			userInteractions = new UserPostInteractions(likeSummary.userInteractions().get().isLiked(),
				likeSummary.userInteractions().get().likedAt(),
				repostSummary.userInteractions().get().isReposted(),
				repostSummary.userInteractions().get().repostedAt());
		}
		return userInteractions;
	}
}
