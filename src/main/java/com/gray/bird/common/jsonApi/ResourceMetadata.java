package com.gray.bird.common.jsonApi;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface ResourceMetadata {
	Map<String, Object> getMetadata();

	Object getMetadata(String key);

	<T> T getMetadata(String key, Class<T> type);

	void addMetadata(String name, Object value);

	void removeMetadata(String key);
}
