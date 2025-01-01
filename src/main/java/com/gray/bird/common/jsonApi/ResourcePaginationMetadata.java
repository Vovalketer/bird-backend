package com.gray.bird.common.jsonApi;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface ResourcePaginationMetadata {
	long getTotalElements();

	int getNumberOfElements();

	boolean isFirst();

	boolean isLast();

	boolean isEmpty();
}
