package com.gray.bird.common.utils;

import org.springframework.stereotype.Component;

import com.gray.bird.common.JsonApiResponse;

@Component
public class JsonApiResponseFactory {
	public <T> JsonApiResponse<T> createResponse(T data) {
		return new JsonApiResponse<T>(data);
	}
}
