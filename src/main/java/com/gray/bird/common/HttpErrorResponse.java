package com.gray.bird.common;

import lombok.Builder;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record HttpErrorResponse(
	String timestamp, int code, String message, Map<String, String> errors, String path) {
}
