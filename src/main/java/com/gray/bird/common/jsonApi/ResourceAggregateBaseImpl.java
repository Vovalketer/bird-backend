package com.gray.bird.common.jsonApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ResourceAggregateBaseImpl implements ResourceAggregateBase {
	private List<ResourceData> included;
	private ResourceMetadata metadata;
	private ResourceLinks links;

	public ResourceAggregateBaseImpl(
		List<ResourceData> included, ResourceMetadata metadata, ResourceLinks links) {
		this.included = included;
		this.metadata = metadata;
		this.links = links;
	}

	@Override
	public List<ResourceData> getIncluded() {
		return included;
	}

	@Override
	public Map<String, Object> getMetadata() {
		return metadata.getMetadata();
	}

	@Override
	public void includeResource(ResourceData data) {
		if (included == null) {
			included = new ArrayList<>();
		}
		included.add(data);
	}

	@Override
	public void includeAllResources(Collection<ResourceData> data) {
		if (included == null) {
			included = new ArrayList<>();
		}
		included.addAll(data);
	}

	@Override
	public void removeIncludedResource(ResourceIdentifier id) {
		included.removeIf(r -> r.idIsEqualTo(id));
	}

	@Override
	public void addMetadata(String key, Object value) {
		metadata.addMetadata(key, value);
	}

	@Override
	public void removeMetadata(String key) {
		metadata.removeMetadata(key);
	}

	@Override
	public String toString() {
		return "ResourceAggregateBaseImpl [included=" + included + ", metadata=" + metadata
			+ ", links=" + links + "]";
	}

	@Override
	public void addLink(String key, String url) {
		links.addLink(key, url);
	}

	@Override
	public Optional<String> getLink(String key) {
		return links.getLink(key);
	}

	@Override
	public void removeLink(String key) {
		links.removeLink(key);
	}

	@Override
	public Map<String, String> getLinks() {
		return links.getLinks();
	}

	@Override
	public Optional<Object> getMetadata(String key) {
		return metadata.getMetadata(key);
	}

	@Override
	public <T> Optional<T> getMetadata(String key, Class<T> type) {
		return metadata.getMetadata(key, type);
	}
}
