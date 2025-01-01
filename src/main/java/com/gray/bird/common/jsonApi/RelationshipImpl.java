package com.gray.bird.common.jsonApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RelationshipImpl implements Relationship {
	private List<ResourceIdentifier> data;
	private Map<String, String> links;

	public RelationshipImpl(List<ResourceIdentifier> data, Map<String, String> links) {
		this.data = data;
		this.links = links;
	}

	public RelationshipImpl(List<ResourceIdentifier> data) {
		this.data = data;
		this.links = null;
	}

	public RelationshipImpl(ResourceIdentifier data) {
		this.data = new ArrayList<ResourceIdentifier>();
		this.data.add(data);
	}

	public RelationshipImpl(ResourceIdentifier data, Map<String, String> links) {
		this.data = new ArrayList<ResourceIdentifier>();
		this.data.add(data);
		this.links = links;
	}

	@Override
	public List<ResourceIdentifier> getData() {
		return data;
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
	public Map<String, String> getLinks() {
		return links;
	}

	@Override
	public String toString() {
		return "RelationshipImpl [data=" + data + ", links=" + links + "]";
	}
}
