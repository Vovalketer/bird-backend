package com.gray.bird.common.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ResourceLinks {
	private Map<String, String> links;

	public ResourceLinks(Map<String, String> links) {
		this.links = links;
	}

	public ResourceLinks() {
		links = new HashMap<>();
	}

	public Map<String, String> getLinks() {
		return links;
	}

	public void addLink(String key, String url) {
		if (links == null) {
			links = new HashMap<>();
		}
		links.put(key, url);
	}

	public Optional<String> getLink(String key) {
		return Optional.ofNullable(links.get(key));
	}

	public void removeLink(String key) {
		links.remove(key);
	}

	@Override
	public String toString() {
		return "ResourceLinks [links=" + links + "]";
	}

	public void setLinks(Map<String, String> links) {
		this.links = links;
	}
}
