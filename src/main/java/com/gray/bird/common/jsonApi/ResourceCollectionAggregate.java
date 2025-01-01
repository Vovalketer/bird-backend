package com.gray.bird.common.jsonApi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface ResourceCollectionAggregate extends ResourceAggregateBase {
	List<ResourceData> getData();

	void addData(ResourceData data);

	void addAllData(List<ResourceData> data);

	void removeData(ResourceIdentifier id);
}
