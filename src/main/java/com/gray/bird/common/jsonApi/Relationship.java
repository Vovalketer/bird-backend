package com.gray.bird.common.jsonApi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface Relationship extends ResourceLinks {
	List<ResourceIdentifier> getData();
}
