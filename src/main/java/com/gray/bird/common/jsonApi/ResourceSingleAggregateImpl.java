package com.gray.bird.common.jsonApi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceSingleAggregateImpl
	extends ResourceAggregateBaseImpl implements ResourceSingleAggregate {
	private ResourceData data;

	public ResourceSingleAggregateImpl(
		ResourceData data, List<ResourceData> included, ResourceMetadata meta, ResourceLinks links) {
		super(included, meta, links);
		this.data = data;
	}

	@Override
	public ResourceData getData() {
		return data;
	}

	@Override
	public String toString() {
		return "ResourceSingleAggregateImpl [data=" + data + "]";
	}

	public void setData(ResourceData data) {
		this.data = data;
	}
}
