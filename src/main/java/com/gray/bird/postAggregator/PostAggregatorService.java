package com.gray.bird.postAggregator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.gray.bird.media.MediaQueryService;
import com.gray.bird.media.dto.MediaProjection;
import com.gray.bird.post.PostService;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.postAggregator.dto.PostInteractions;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostAggregatorService {
	private final PostService postService;
	private final MediaQueryService mediaQueryService;
	private final PostInteractionsService interactionsQueryService;

	public PostAggregate getPost(Long id) {
		PostProjection post = postService.getPostById(id);
		List<MediaProjection> media = new ArrayList<>();
		if (post.hasMedia()) {
			media.addAll(mediaQueryService.getMediaByPostId(id));
		}
		PostInteractions interactions = interactionsQueryService.getInteractionsById(id);
		PostAggregate aggregate = new PostAggregate(post, media, Optional.of(interactions));

		return aggregate;
	}

	public List<PostAggregate> getPosts(Collection<Long> ids) {
		List<PostProjection> posts = postService.getAllPostsById(ids);
		List<MediaProjection> media = mediaQueryService.getAllMediaByPostId(ids);
		List<PostInteractions> interactions = interactionsQueryService.getAllInteractionsByIds(ids);
		List<PostAggregate> packagedPosts = packagePosts(posts, media, interactions);
		return packagedPosts;
	}

	private List<PostAggregate> packagePosts(
		List<PostProjection> posts, List<MediaProjection> media, Collection<PostInteractions> interactions) {
		List<PostAggregate> res = new LinkedList<>();
		for (PostProjection post : posts) {
			List<MediaProjection> postMedia =
				media.stream().filter(m -> m.postId() == post.id()).collect(Collectors.toList());
			Optional<PostInteractions> postInteractions =
				interactions.stream().filter(i -> i.postId() == post.id()).findFirst();
			res.add(new PostAggregate(post, postMedia, postInteractions));
		}
		return res;
	}
}
