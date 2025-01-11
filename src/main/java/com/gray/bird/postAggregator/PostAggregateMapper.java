package com.gray.bird.postAggregator;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

import com.gray.bird.media.MediaMapper;
import com.gray.bird.post.PostEntity;
import com.gray.bird.post.PostMapper;
import com.gray.bird.postAggregator.dto.PostInteractions;

@Mapper(uses = {PostMapper.class, MediaMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PostAggregateMapper {
	@Mapping(target = "interactions", expression = "java(mapInteractions(post))")
	@Mapping(target = "post", source = "post")
	PostAggregate toPostAggregate(PostEntity post);

	default Optional<PostInteractions> mapInteractions(PostEntity post) {
		if (post == null) {
			return Optional.empty();
		}
		long likesCount = 0;
		long repostsCount = 0;
		long repliesCount = 0;
		return Optional.of(new PostInteractions(post.getId(), repliesCount, likesCount, repostsCount));
	}

	default<T> Optional<T> wrapOptional(T t) {
		return Optional.ofNullable(t);
	}
}
