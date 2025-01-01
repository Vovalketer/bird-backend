package com.gray.bird.common.jsonApi;

import java.util.Map;

public class ResourceLinksImpl implements ResourceLinks {
	private Map<String, String> links;

	@Override
	public Map<String, String> getLinks() {
		return links;
	}

	@Override
	public void addLink(String name, String url) {
		links.put(name, url);
	}

	@Override
	public String getLink(String name) {
		return links.get(name);
	}

	@Override
	public void removeLink(String name) {
		links.remove(name);
	}

	@Override
	public String toString() {
		return "ResourceLinksImpl [links=" + links + "]";
	}
}
