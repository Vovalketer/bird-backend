package com.gray.bird.common.jsonApi;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ResourceLinks {
	void addLink(String name, String url);

	String getLink(String name);

	void removeLink(String name);

	Map<String, String> getLinks();
}
