package com.gray.bird.common.jsonApi;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ResourceRelationshipsImpl implements ResourceRelationships {
	private Map<String, RelationshipToOne> relationshipsToOne;
	private Map<String, RelationshipToMany> relationshipsToMany;

	public ResourceRelationshipsImpl() {
		this.relationshipsToOne = new HashMap<>();
		this.relationshipsToMany = new HashMap<>();
	}

	@Override
	public void addRelationshipToOne(String key, RelationshipToOne relationship) {
		relationshipsToOne.put(key, relationship);
	}

	@Override
	public void addRelationshipToMany(String key, RelationshipToMany relationship) {
		relationshipsToMany.put(key, relationship);
	}

	@Override
	public Optional<RelationshipToOne> getRelationshipToOne(String key) {
		return Optional.ofNullable(relationshipsToOne.get(key));
	}

	@Override
	public Optional<RelationshipToMany> getRelationshipToMany(String key) {
		return Optional.ofNullable(relationshipsToMany.get(key));
	}

	@Override
	public Map<String, Object> getRelationships() {
		Map<String, Object> relationships = new HashMap<>();
		relationshipsToOne.forEach((key, relationship) -> relationships.put(key, relationship));
		relationshipsToMany.forEach((key, relationship) -> relationships.put(key, relationship));
		return relationships;
	}

	@Override
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
		return "ResourceRelationshipsImpl [relationshipsToOne=" + relationshipsToOne
			+ ", relationshipsToMany=" + relationshipsToMany + "]";
	}
}
