package com.gray.bird.common.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceSingleAggregate extends ResourceAggregateBase {
	private ResourceData data;

	public ResourceSingleAggregate(
		ResourceData data, List<ResourceData> included, ResourceMetadata meta, ResourceLinks links) {
		super(included, meta, links);
		this.data = data;
	}

	public ResourceSingleAggregate() {
		super();
	}

	public ResourceData getData() {
		return data;
	}

	@Override
	public String toString() {
		return "ResourceSingleAggregate [data=" + data + "]";
	}

	public void setData(ResourceData data) {
		this.data = data;
	}
}
