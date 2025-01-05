package com.gray.bird.common.jsonApi;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ResourceAttributes {
	Map<String, Object> getAttributes();

	Object getAttribute(String key);

	<T> T getAttribute(String key, Class<T> type);
}
