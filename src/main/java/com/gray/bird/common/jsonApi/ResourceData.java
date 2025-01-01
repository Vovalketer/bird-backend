package com.gray.bird.common.jsonApi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"type", "id", "attributes", "relationships", "links", "meta"})
public interface ResourceData
	extends ResourceIdentifier, ResourceAttributes, ResourceRelationships, ResourceLinks, ResourceMetadata {}
