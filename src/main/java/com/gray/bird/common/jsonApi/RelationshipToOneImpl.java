package com.gray.bird.common.jsonApi;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RelationshipToOneImpl implements RelationshipToOne {
	private ResourceIdentifier data;
	private Map<String, String> links;
	private Map<String, Object> metadata;

	public RelationshipToOneImpl(
		ResourceIdentifier data, Map<String, String> links, Map<String, Object> metadata) {
		this.data = data;
		this.links = links;
		this.metadata = metadata;
	}

	public RelationshipToOneImpl(ResourceIdentifier data) {
		this.data = data;
		this.links = new HashMap<>();
		this.metadata = new HashMap<>();
	}

	public RelationshipToOneImpl() {
		this.links = new HashMap<>();
		this.metadata = new HashMap<>();
	}

	@Override
	public ResourceIdentifier getData() {
		return data;
	}

	public void setData(ResourceIdentifier data) {
		this.data = data;
	}

	@Override
	public void addLink(String type, String url) {
		links.put(type, url);
	}

	@Override
	public Optional<String> getLink(String type) {
		return Optional.ofNullable(links.get(type));
	}

	@Override
	public void removeLink(String type) {
		links.remove(type);
	}

	@Override
	public Map<String, String> getLinks() {
		return links;
	}

	@Override
	public Map<String, Object> getMetadata() {
		return metadata;
	}

	@Override
	public Optional<Object> getMetadata(String type) {
		return Optional.ofNullable(metadata.get(type));
	}

	@Override
	public <T> Optional<T> getMetadata(String type, Class<T> classType) {
		Object object = metadata.get(type);
		if (object != null) {
			return Optional.of(classType.cast(object));
		} else {
			return Optional.empty();
		}
	}

	@Override
	public void addMetadata(String type, Object value) {
		metadata.put(type, value);
	}

	@Override
	public void removeMetadata(String type) {
		metadata.remove(type);
	}
}
