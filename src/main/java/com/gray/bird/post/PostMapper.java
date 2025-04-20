package com.gray.bird.post;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import com.gray.bird.media.MediaMapper;
import com.gray.bird.post.dto.PostProjection;

@Mapper(uses = MediaMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PostMapper {
	PostProjection toPostProjection(PostEntity post);
}
