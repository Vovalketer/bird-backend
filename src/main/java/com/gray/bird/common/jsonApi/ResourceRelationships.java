package com.gray.bird.common.jsonApi;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface ResourceRelationships {
	Optional<RelationshipToOne> getRelationshipToOne(String key);

	Optional<RelationshipToMany> getRelationshipToMany(String key);

	Map<String, Object> getRelationships();

	void addRelationshipToOne(String key, RelationshipToOne relationship);

	void addRelationshipToMany(String key, RelationshipToMany relationship);

	void removeRelationship(String key);
}
