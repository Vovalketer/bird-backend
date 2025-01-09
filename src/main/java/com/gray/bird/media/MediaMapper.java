package com.gray.bird.media;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

import com.gray.bird.media.dto.MediaDto;
import com.gray.bird.media.dto.MediaProjection;
import com.gray.bird.media.dto.MediaRequest;
import com.gray.bird.media.dto.MediaRequestContent;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MediaMapper {
	MediaDto toMediaDto(MediaEntity media);

	List<MediaDto> toMediaDto(Set<MediaEntity> media);

	MediaProjection toMediaProjection(MediaEntity media);

	List<MediaProjection> toMediaProjection(List<MediaEntity> media);

	MediaEntity toMediaEntity(MediaRequest media);

	MediaEntity toMediaEntity(MediaRequestContent media);
}
