package com.gray.bird.common.json;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ResourceResponseFactory {
	private final ResourceFactory resourceFactory;

	public ResourceSingleAggregate createResponse(ResourceData data, List<ResourceData> included) {
		ResourceSingleAggregate res = initializeSingleAggregate();
		res.setData(data);
		res.includeAllResources(included);
		return res;
	}

	public ResourceSingleAggregate createResponse(ResourceData data, ResourceData included) {
		ResourceSingleAggregate res = initializeSingleAggregate();
		res.setData(data);
		res.includeResource(included);
		return res;
	}

	public ResourceSingleAggregate createResponse(ResourceData data) {
		ResourceSingleAggregate res = initializeSingleAggregate();
		res.setData(data);
		return res;
	}

	public ResourceCollectionAggregate createResponse(List<ResourceData> data, List<ResourceData> included) {
		ResourceCollectionAggregate res = initializeCollectionAggregate();
		res.addAllData(data);
		res.includeAllResources(included);
		return res;
	}

	public ResourceCollectionAggregate createResponse(List<ResourceData> data, ResourceData included) {
		ResourceCollectionAggregate res = initializeCollectionAggregate();
		res.addAllData(data);
		res.includeResource(included);
		return res;
	}

	public ResourceCollectionAggregate createResponse(List<ResourceData> data) {
		ResourceCollectionAggregate res = initializeCollectionAggregate();
		res.addAllData(data);
		return res;
	}

	private ResourceSingleAggregate initializeSingleAggregate() {
		ResourceMetadata metadata = resourceFactory.createMetadata();
		ResourceLinks links = resourceFactory.createLinks();
		List<ResourceData> included = new ArrayList<>();
		return new ResourceSingleAggregate(null, included, metadata, links);
	}

	private ResourceCollectionAggregate initializeCollectionAggregate() {
		List<ResourceData> data = new ArrayList<>();
		List<ResourceData> included = new ArrayList<>();
		ResourceMetadata metadata = resourceFactory.createMetadata();
		ResourceLinks links = resourceFactory.createLinks();
		return new ResourceCollectionAggregate(data, included, metadata, links);
	}
}
