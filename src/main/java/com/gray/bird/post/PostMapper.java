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
	@Mapping(target = "media", ignore = true)
	PostEntity toPostEntity(PostRequest post);

	// no interactions mapped, might deprecate PostDto later
	PostDto toPostDto(PostEntity post);

	PostView toPostView(PostEntity post);

	@Mapping(target = "userId", source = "post.user.id")
	@Mapping(target = "userReferenceId", source = "post.user.referenceId")
	PostProjection toPostProjection(PostEntity post);
}
