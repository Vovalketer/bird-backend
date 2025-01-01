package com.gray.bird.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@Deprecated
public class PageResponse<T> {
	// ResponseStatus status;
	private T data;
	private PaginationMetadata meta;
}
