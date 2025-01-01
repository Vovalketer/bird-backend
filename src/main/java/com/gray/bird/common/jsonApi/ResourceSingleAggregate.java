package com.gray.bird.common.jsonApi;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface ResourceSingleAggregate extends ResourceAggregateBase {
	ResourceData getData();
}
