package com.gray.bird.postAggregator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gray.bird.media.MediaQueryService;
import com.gray.bird.media.dto.MediaDto;
import com.gray.bird.post.PostService;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.postAggregator.dto.PostEngagement;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostAggregatorService {
	private final PostService postService;
	private final MediaQueryService mediaQueryService;
	private final PostInteractionsService interactionsQueryService;

	public PostAggregate getPost(Long id, UUID userId) {
		PostProjection post = postService.getPostById(id);
		List<MediaDto> media = new ArrayList<>();
		if (post.hasMedia()) {
			media.addAll(mediaQueryService.getMediaByPostId(id));
		}
		PostEngagement engagement = interactionsQueryService.getInteractionsById(id, userId);
		PostAggregate aggregate = new PostAggregate(post, media, engagement);

		return aggregate;
	}

	public List<PostAggregate> getPosts(Collection<Long> ids, UUID userId) {
		List<PostProjection> posts = postService.getAllPostsById(ids);
		List<MediaDto> media = mediaQueryService.getAllMediaByPostId(ids);
		List<PostEngagement> engagement = interactionsQueryService.getAllInteractionsByIds(ids, userId);
		List<PostAggregate> packagedPosts = packagePosts(posts, media, engagement);
		return packagedPosts;
	}

	private List<PostAggregate> packagePosts(
		List<PostProjection> posts, List<MediaDto> media, List<PostEngagement> interactions) {
		List<PostAggregate> res = new LinkedList<>();
		for (PostProjection post : posts) {
			List<MediaDto> postMedia =
				media.stream().filter(m -> m.postId().equals(post.id())).collect(Collectors.toList());
			Optional<PostEngagement> postInteractions =
				interactions.stream().filter(i -> i.postId().equals(post.id())).findAny();
			res.add(new PostAggregate(post, postMedia, postInteractions.get()));
		}
		return res;
	}
}
