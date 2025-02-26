package com.gray.bird.postAggregator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import com.gray.bird.common.json.ResourceMapper;
import com.gray.bird.post.dto.PostAttributes;
import com.gray.bird.post.dto.PostRelationships;
import com.gray.bird.post.dto.PostResource;

@Component
public class PostAggregateResourceMapper implements ResourceMapper<PostAggregate, PostResource> {
	private static final String INTERACTIONS = "interactions";

	@Override
	public PostResource toResource(PostAggregate data) {
		PostResource resource = new PostResource(data.post().id(), toAttributes(data), toRelationship(data));

		resource.addMetadata(INTERACTIONS, data.engagement());

		return resource;
	}

	// duplicated from PostResourceMapper but has to be this way to keep it decoupled
	// or else the relationships would have to be mutable and this would have to have dependencies
	private PostAttributes toAttributes(PostAggregate data) {
		return new PostAttributes(data.post().text(), data.post().replyType(), data.post().createdAt());
	}

	private PostRelationships toRelationship(PostAggregate data) {
		List<Long> mediaIds = new ArrayList<>();
		if (data.media() != null) {
			mediaIds.addAll(data.media().stream().map(media -> media.id()).toList());
		}
		return new PostRelationships(data.post().userId().toString(), data.post().parentPostId(), mediaIds);
	}
}
