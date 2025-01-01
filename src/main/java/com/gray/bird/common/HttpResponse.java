package com.gray.bird.common;

import lombok.Builder;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Builder
public record HttpResponse<T>(int code, T data, String message, String timestamp, String path) {
}
