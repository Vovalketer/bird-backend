package com.gray.bird.common.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResourceRelationships {
	private Map<String, RelationshipToOne> relationshipsToOne;
	private Map<String, RelationshipToMany> relationshipsToMany;

	public ResourceRelationships() {
		this.relationshipsToOne = new HashMap<>();
		this.relationshipsToMany = new HashMap<>();
	}

	public void addRelationshipToOne(String key, RelationshipToOne relationship) {
		relationshipsToOne.put(key, relationship);
	}

	public void addRelationshipToMany(String key, RelationshipToMany relationship) {
		relationshipsToMany.put(key, relationship);
	}

	public Optional<RelationshipToOne> getRelationshipToOne(String key) {
		return Optional.ofNullable(relationshipsToOne.get(key));
	}

	public Optional<RelationshipToMany> getRelationshipToMany(String key) {
		return Optional.ofNullable(relationshipsToMany.get(key));
	}

	public Map<String, Object> getRelationships() {
		Map<String, Object> relationships = new HashMap<>();
		relationshipsToOne.forEach((key, relationship) -> relationships.put(key, relationship));
		relationshipsToMany.forEach((key, relationship) -> relationships.put(key, relationship));
		return relationships;
	}

	public void removeRelationship(String key) {
		// attempt to remove a relationship in the To Many map
		// if the key isnt found within the To One relationships
		RelationshipToOne remove = relationshipsToOne.remove((key));
		if (remove == null) {
			relationshipsToMany.remove(key);
		}
	}

	@Override
	public String toString() {
		return "ResourceRelationships [relationshipsToOne=" + relationshipsToOne
			+ ", relationshipsToMany=" + relationshipsToMany + "]";
	}
}
