package com.gray.bird.common.json;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class ResourceFactory {
	private final ObjectMapper mapper;

	public ResourceIdentifier createIdentifier(String type, String id) {
		return new ResourceIdentifier(type, id);
	}

	private ResourceData initializeData(ResourceIdentifier id, ResourceAttributes attributes) {
		ResourceRelationships relationships = createRelationships();
		ResourceMetadata metadata = createMetadata();
		ResourceLinks links = createLinks();
		var res = new ResourceData(id, attributes, relationships, links, metadata);
		return res;
	}

	public ResourceData createData(ResourceIdentifier id, ResourceAttributes attributes) {
		ResourceData content = initializeData(id, attributes);
		return content;
	}

	public ResourceData createData(String type, String id, ResourceAttributes attributes) {
		ResourceIdentifier identifier = createIdentifier(type, id);
		ResourceData content = initializeData(identifier, attributes);
		return content;
	}

	public ResourceRelationships createRelationships() {
		return new ResourceRelationships();
	}

	public RelationshipToOne createRelationshipToOne(ResourceIdentifier data) {
		return new RelationshipToOne(data);
	}

	public RelationshipToMany createRelationshipToMany(List<ResourceIdentifier> data) {
		return new RelationshipToMany(data);
	}

	public ResourceAttributes createAttributes(Object attributes) {
		@SuppressWarnings("unchecked")
		Map<String, Object> convertValue = mapper.convertValue(attributes, Map.class);
		return new ResourceAttributes(convertValue);
	}

	public ResourceLinks createLinks() {
		return new ResourceLinks();
	}

	public ResourceMetadata createMetadata() {
		return new ResourceMetadata();
	}

	public List<ResourceIdentifier> getIdentifiers(List<ResourceData> data) {
		return data.stream().map(ResourceData::getResourceIdentifier).toList();
	}
}
