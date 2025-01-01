package com.gray.bird.postAggregate;

import java.util.List;
import java.util.Optional;

import com.gray.bird.media.dto.MediaProjection;
import com.gray.bird.post.dto.PostProjection;

public record PostAggregate(PostProjection post, List<MediaProjection> media,
	Optional<InteractionsAggregate> interactions) {
}
