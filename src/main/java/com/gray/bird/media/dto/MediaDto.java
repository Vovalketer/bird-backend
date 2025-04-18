package com.gray.bird.media.dto;

import org.springframework.http.MediaType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
@Builder
@AllArgsConstructor
public class MediaDto {
	private Long id;
	private Long postId;
	private UUID userId;
	private String url;
	private String alt;
	private int width;
	private int height;
	private long size;
	private Integer duration;
	private MediaType type;
}
