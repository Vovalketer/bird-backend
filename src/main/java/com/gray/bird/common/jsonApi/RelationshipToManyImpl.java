package com.gray.bird.common.jsonApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RelationshipToManyImpl implements RelationshipToMany {
	private List<ResourceIdentifier> data;
	private Map<String, String> links;
	private Map<String, Object> metadata;

	public RelationshipToManyImpl(
		List<ResourceIdentifier> data, Map<String, String> links, Map<String, Object> metadata) {
		this.data = data;
		this.links = links;
		this.metadata = metadata;
	}

	public RelationshipToManyImpl(List<ResourceIdentifier> data) {
		this.data = data;
		this.links = new HashMap<>();
		this.metadata = new HashMap<>();
	}

	public RelationshipToManyImpl() {
		this.data = new ArrayList<>();
		this.links = new HashMap<>();
		this.metadata = new HashMap<>();
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

	@Override
	public List<ResourceIdentifier> getData() {
		return data;
	}

	@Override
	public void addData(ResourceIdentifier data) {
		this.data.add(data);
	}

	@Override
	public Boolean isPresent(ResourceIdentifier resourceIdentifier) {
		return data.contains(resourceIdentifier);
	}
}
