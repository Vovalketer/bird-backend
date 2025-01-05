package com.gray.bird.common.jsonApi;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ResourceLinks {
	void addLink(String key, String url);

	Optional<String> getLink(String key);

	void removeLink(String key);

	Map<String, String> getLinks();
}
