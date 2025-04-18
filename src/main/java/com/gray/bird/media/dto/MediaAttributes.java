package com.gray.bird.media.dto;

import org.springframework.http.MediaType;

public record MediaAttributes(
	String url, String alt, int width, int height, long size, long duration, MediaType type) {
}
