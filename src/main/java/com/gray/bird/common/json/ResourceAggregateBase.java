package com.gray.bird.common.json;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor
public class ResourceAggregateBase {
	private List<ResourceData> included;
	private ResourceMetadata metadata;
	private ResourceLinks links;

	public ResourceAggregateBase(
		List<ResourceData> included, ResourceMetadata metadata, ResourceLinks links) {
		this.included = included;
		this.metadata = metadata;
		this.links = links;
	}

	public ResourceAggregateBase(List<ResourceData> included) {
		this.included = included;
		this.metadata = new ResourceMetadata();
		this.links = new ResourceLinks();
	}

	public List<ResourceData> getIncluded() {
		return included;
	}

	public Map<String, Object> getMetadata() {
		return metadata.getMetadata();
	}

	public void includeResource(ResourceData data) {
		if (included == null) {
			included = new ArrayList<>();
		}
		included.add(data);
	}

	public void includeAllResources(Collection<ResourceData> data) {
		if (included == null) {
			included = new ArrayList<>();
		}
		included.addAll(data);
	}

	public void removeIncludedResource(ResourceIdentifier id) {
		included.removeIf(r -> r.getResourceIdentifier().equals(id));
	}

	public void addMetadata(String key, Object value) {
		metadata.addMetadata(key, value);
	}

	public void removeMetadata(String key) {
		metadata.removeMetadata(key);
	}

	@Override
	public String toString() {
		return "ResourceAggregateBase [included=" + included + ", metadata=" + metadata + ", links=" + links
			+ "]";
	}

	public void addLink(String key, String url) {
		links.addLink(key, url);
	}

	public Optional<String> getLink(String key) {
		return links.getLink(key);
	}

	public void removeLink(String key) {
		links.removeLink(key);
	}

	public Map<String, String> getLinks() {
		return links.getLinks();
	}

	public Optional<Object> getMetadata(String key) {
		return metadata.getMetadata(key);
	}

	public <T> Optional<T> getMetadata(String key, Class<T> type) {
		return metadata.getMetadata(key, type);
	}
}
