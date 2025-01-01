package com.gray.bird.common.jsonApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceRelationshipsImpl implements ResourceRelationships {
	private Map<String, List<Relationship>> relationships;

	public ResourceRelationshipsImpl(Map<String, List<Relationship>> relationships) {
		this.relationships = relationships;
	}

	public ResourceRelationshipsImpl() {
		this.relationships = new HashMap<>();
	}

	@Override
	public List<Relationship> getRelationshipsForType(String type) {
		return relationships.get(type);
	}

	@Override
	public Map<String, List<Relationship>> getRelationships() {
		return relationships;
	}

	@Override
	public void addRelationship(String name, Relationship relationship) {
		relationships.computeIfAbsent(name, r -> new ArrayList<>()).add(relationship);
	}

	@Override
	public void removeRelationship(String name) {
		relationships.remove(name);
	}

	@Override
	public String toString() {
		return "ResourceRelationshipsImpl [relationships=" + relationships + "]";
	}
}
