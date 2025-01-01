package com.gray.bird.media.dto;

public record MediaAttributes(
	String url, String description, int width, int height, long fileSize, long duration, String format) {
}
