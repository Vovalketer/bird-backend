package com.gray.bird.media;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

import com.gray.bird.media.dto.MediaDto;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MediaMapper {
	MediaDto toMediaDto(MediaEntity media);

	List<MediaDto> toMediaDto(Set<MediaEntity> media);
}
