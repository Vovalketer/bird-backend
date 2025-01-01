package com.gray.bird.common.jsonApi;

import java.util.ArrayList;
import java.util.List;

public class ResourceCollectionAggregateImpl
	extends ResourceAggregateBaseImpl implements ResourceCollectionAggregate {
	private List<ResourceData> data;

	public ResourceCollectionAggregateImpl(
		List<ResourceData> data, List<ResourceData> included, ResourceMetadata meta) {
		super(included, meta);
		this.data = data;
	}

	public ResourceCollectionAggregateImpl(List<ResourceData> data, List<ResourceData> included) {
		super(included);
		this.data = data;
	}

	public ResourceCollectionAggregateImpl(List<ResourceData> data, ResourceMetadata meta) {
		super(meta);
		this.data = data;
	}

	public ResourceCollectionAggregateImpl(List<ResourceData> data) {
		super();
		this.data = data;
	}

	public ResourceCollectionAggregateImpl() {
		super();
		this.data = new ArrayList<>();
	}

	@Override
	public List<ResourceData> getData() {
		return data;
	}

	@Override
	public void addData(ResourceData data) {
		this.data.add(data);
	}

	@Override
	public void addAllData(List<ResourceData> data) {
		this.data.addAll(data);
	}

	@Override
	public void removeData(ResourceIdentifier id) {
		this.data.removeIf(d -> d.idIsEqualTo(id));
	}

	@Override
	public String toString() {
		return "ResourceCollectionAggregateImpl [data=" + data + "]";
	}
}
