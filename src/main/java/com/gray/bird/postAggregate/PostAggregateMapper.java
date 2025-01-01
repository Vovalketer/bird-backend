package com.gray.bird.postAggregate;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

import com.gray.bird.media.MediaMapper;
import com.gray.bird.post.PostEntity;
import com.gray.bird.post.PostMapper;

@Mapper(uses = {PostMapper.class, MediaMapper.class})
public interface PostAggregateMapper {
	@Mapping(target = "interactions", expression = "java(mapInteractions(post))")
	@Mapping(target = "post", source = "post")
	PostAggregate toPostAggregate(PostEntity post);

	default Optional<InteractionsAggregate> mapInteractions(PostEntity post) {
		if (post == null) {
			return Optional.empty();
		}
		long likesCount = post.getLikes() != null ? post.getLikes().size() : 0;
		long repostsCount = post.getReposts() != null ? post.getReposts().size() : 0;
		long repliesCount = post.getReplies() != null ? post.getReplies().size() : 0;
		return Optional.of(
			new InteractionsAggregate(post.getId(), repliesCount, likesCount, repostsCount));
	}

	default<T> Optional<T> wrapOptional(T t) {
		return Optional.ofNullable(t);
	}
}
