package com.gray.bird.post;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gray.bird.media.MediaMapper;
import com.gray.bird.post.dto.PostDto;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.post.dto.PostRequest;
import com.gray.bird.post.view.PostView;

@Mapper(uses = MediaMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PostMapper {
	PostEntity toPostEntity(PostRequest post);

	// no interactions mapped, might deprecate PostDto later
	PostDto toPostDto(PostEntity post);

	PostProjection toPostProjection(PostEntity post);
}
