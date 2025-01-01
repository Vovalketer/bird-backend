package com.gray.bird.common.jsonApi;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface ResourceRelationships {
	List<Relationship> getRelationshipsForType(String type);

	Map<String, List<Relationship>> getRelationships();

	void addRelationship(String name, Relationship relationship);

	void removeRelationship(String name);
}
