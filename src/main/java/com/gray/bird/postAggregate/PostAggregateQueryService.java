package com.gray.bird.postAggregate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.gray.bird.media.MediaQueryService;
import com.gray.bird.media.dto.MediaProjection;
import com.gray.bird.post.PostQueryService;
import com.gray.bird.post.dto.PostProjection;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostAggregateQueryService {
	private final PostQueryService postQueryService;
	private final MediaQueryService mediaQueryService;
	private final InteractionsAggregateQueryService interactionsQueryService;

	public PostAggregate getPost(Long id) {
		PostProjection post = postQueryService.getPostById(id);
		List<MediaProjection> media = mediaQueryService.getMediaByPostId(id);
		InteractionsAggregate interactions = interactionsQueryService.getInteractionsById(id);
		PostAggregate aggregate = new PostAggregate(post, media, Optional.of(interactions));

		return aggregate;
	}

	public List<PostAggregate> getPosts(Iterable<Long> ids) {
		List<PostProjection> posts = postQueryService.getAllPostsById(ids);
		List<MediaProjection> media = mediaQueryService.getAllMediaByPostId(ids);
		List<InteractionsAggregate> interactions = interactionsQueryService.getAllInteractionsByIds(ids);
		List<PostAggregate> packagedPosts = packagePosts(posts, media, interactions);
		return packagedPosts;
	}

	private List<PostAggregate> packagePosts(List<PostProjection> posts, List<MediaProjection> media,
		Collection<InteractionsAggregate> interactions) {
		List<PostAggregate> res = new LinkedList<>();
		for (PostProjection post : posts) {
			List<MediaProjection> postMedia =
				media.stream().filter(m -> m.postId() == post.id()).collect(Collectors.toList());
			Optional<InteractionsAggregate> postInteractions =
				interactions.stream().filter(i -> i.postId() == post.id()).findFirst();
			res.add(new PostAggregate(post, postMedia, postInteractions));
		}
		return res;
	}
}
