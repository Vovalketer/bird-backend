package com.gray.bird.media.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
@Builder
@AllArgsConstructor
public class MediaDto {
	private Long id;
	private Long postId;
	private String url;
	private String description;
	private int width;
	private int height;
	private long fileSize;
	private long duration;
	private String format;
}
