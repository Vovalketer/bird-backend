package com.gray.bird.post;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gray.bird.media.MediaMapper;
import com.gray.bird.post.dto.PostDto;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.post.dto.PostRequest;

@Mapper(uses = MediaMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PostMapper {
	PostEntity toPostEntity(PostRequest post);

	PostProjection toPostProjection(PostEntity post);
}
