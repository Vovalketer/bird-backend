package com.gray.bird.common.utils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

import com.gray.bird.common.json.ResourceError;
import com.gray.bird.common.json.ResourceErrorResponse;
import com.gray.bird.common.json.ResourceErrorSource;

@Component
public class JsonApiErrorFactory {
	public ResourceErrorResponse createErrorResponse(Collection<ResourceError> errors) {
		return new ResourceErrorResponse(errors);
	}

	public ResourceErrorResponse createErrorResponse(ResourceError error) {
		return new ResourceErrorResponse(Collections.singletonList(error));
	}

	public ResourceError createError(
		HttpStatus status, String title, String detail, ResourceErrorSource source) {
		return new ResourceError(String.valueOf(status.value()), title, detail, source);
	}

	public ResourceError createError(HttpStatus status, String title, String detail) {
		return new ResourceError(String.valueOf(status.value()), title, detail);
	}

	public ResourceErrorSource createErrorSource(String pointer, String parameter, String header) {
		return new ResourceErrorSource(pointer, parameter, header);
	}
}
