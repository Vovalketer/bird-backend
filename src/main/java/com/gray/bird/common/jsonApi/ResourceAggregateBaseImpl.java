package com.gray.bird.common.jsonApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResourceAggregateBaseImpl implements ResourceAggregateBase {
	private List<ResourceData> included;
	private ResourceMetadata meta;

	public ResourceAggregateBaseImpl(List<ResourceData> included, ResourceMetadata meta) {
		this.included = included;
		this.meta = meta;
	}

	public ResourceAggregateBaseImpl(List<ResourceData> included) {
		this.included = included;
	}

	public ResourceAggregateBaseImpl(ResourceMetadata meta) {
		this.meta = meta;
	}

	public ResourceAggregateBaseImpl() {
	}

	@Override
	public List<ResourceData> getIncluded() {
		return included;
	}

	@Override
	public ResourceMetadata getMeta() {
		return meta;
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
		meta.addMetadata(key, value);
	}

	@Override
	public void removeMetadata(String key) {
		meta.removeMetadata(key);
	}

	@Override
	public String toString() {
		return "ResourceAggregateBaseImpl [included=" + included + ", meta=" + meta + "]";
	}
}
