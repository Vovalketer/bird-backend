package com.gray.bird.common.jsonApi;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface ResourceAggregateBase extends ResourceMetadata, ResourceLinks {
	List<ResourceData> getIncluded();

	void includeResource(ResourceData data);

	void includeAllResources(Collection<ResourceData> data);

	void removeIncludedResource(ResourceIdentifier id);
}
