package com.gray.bird.common.jsonApi;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ResourceResponseFactory {
	public ResourceSingleAggregate createResponse(ResourceData data, List<ResourceData> included) {
		ResourceSingleAggregateImpl res = initializeSingleAggregate();
		res.setData(data);
		res.includeAllResources(included);
		return res;
	}

	public ResourceSingleAggregate createResponse(ResourceData data, ResourceData included) {
		ResourceSingleAggregateImpl res = initializeSingleAggregate();
		res.includeResource(included);
		return res;
	}

	public ResourceSingleAggregate createResponse(ResourceData data) {
		ResourceSingleAggregateImpl res = initializeSingleAggregate();
		res.setData(data);
		return res;
	}

	public ResourceCollectionAggregate createResponse(List<ResourceData> data, List<ResourceData> included) {
		ResourceCollectionAggregateImpl res = initializeCollectionAggregate();
		res.addAllData(data);
		res.includeAllResources(included);
		return res;
	}

	public ResourceCollectionAggregate createResponse(List<ResourceData> data, ResourceData included) {
		ResourceCollectionAggregateImpl res = initializeCollectionAggregate();
		res.addAllData(data);
		res.includeResource(included);
		return res;
	}

	public ResourceCollectionAggregate createResponse(List<ResourceData> data) {
		ResourceCollectionAggregateImpl res = initializeCollectionAggregate();
		res.addAllData(data);
		return res;
	}

	private ResourceSingleAggregateImpl initializeSingleAggregate() {
		ResourceMetadata metadata = new ResourceMetadataImpl();
		ResourceLinks links = new ResourceLinksImpl();
		List<ResourceData> included = new ArrayList<>();
		return new ResourceSingleAggregateImpl(null, included, metadata, links);
	}

	private ResourceCollectionAggregateImpl initializeCollectionAggregate() {
		List<ResourceData> included = new ArrayList<>();
		ResourceMetadata metadata = new ResourceMetadataImpl();
		ResourceLinks links = new ResourceLinksImpl();
		return new ResourceCollectionAggregateImpl(null, included, metadata, links);
	}
}
