package com.gray.bird.common.json;

import java.util.List;

public class ResourceCollectionAggregate extends ResourceAggregateBase {
	private List<ResourceData> data;

	public ResourceCollectionAggregate(List<ResourceData> data, List<ResourceData> included,
		ResourceMetadata metadata, ResourceLinks links) {
		super(included, metadata, links);
		this.data = data;
	}

	public List<ResourceData> getData() {
		return data;
	}

	public void addData(ResourceData data) {
		this.data.add(data);
	}

	public void addAllData(List<ResourceData> data) {
		this.data.addAll(data);
	}

	public void removeData(ResourceIdentifier id) {
		this.data.removeIf(r -> r.getResourceIdentifier().equals(id));
	}

	@Override
	public String toString() {
		return "ResourceCollectionAggregate [data=" + data + "]";
	}

	public void setData(List<ResourceData> data) {
		this.data = data;
	}
}
