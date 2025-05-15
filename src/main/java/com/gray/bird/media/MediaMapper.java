package com.gray.bird.media;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import com.gray.bird.media.dto.MediaDto;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MediaMapper {
	MediaDto toMediaDto(MediaEntity media);
}
