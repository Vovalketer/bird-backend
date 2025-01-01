package com.gray.bird.common.jsonApi;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface ResourceAggregateBase {
	List<ResourceData> getIncluded();

	ResourceMetadata getMeta();

	void includeResource(ResourceData data);

	void includeAllResources(Collection<ResourceData> data);

	void removeIncludedResource(ResourceIdentifier id);

	void addMetadata(String key, Object value);

	void removeMetadata(String key);
}
