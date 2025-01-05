package com.gray.bird.common.jsonApi;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ResourceLinksImpl implements ResourceLinks {
	private Map<String, String> links;

	public ResourceLinksImpl(Map<String, String> links) {
		this.links = links;
	}

	public ResourceLinksImpl() {
		links = new HashMap<>();
	}

	@Override
	public Map<String, String> getLinks() {
		return links;
	}

	@Override
	public void addLink(String key, String url) {
		links.put(key, url);
	}

	@Override
	public Optional<String> getLink(String key) {
		return Optional.ofNullable(links.get(key));
	}

	@Override
	public void removeLink(String key) {
		links.remove(key);
	}

	@Override
	public String toString() {
		return "ResourceLinksImpl [links=" + links + "]";
	}

	public void setLinks(Map<String, String> links) {
		this.links = links;
	}
}
