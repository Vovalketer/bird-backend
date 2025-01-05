package com.gray.bird.common.jsonApi;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface ResourceMetadata {
	Map<String, Object> getMetadata();

	Optional<Object> getMetadata(String key);

	<T> Optional<T> getMetadata(String key, Class<T> type);

	void addMetadata(String key, Object value);

	void removeMetadata(String key);
}
