package com.gray.bird.common.jsonApi;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class ResourceFactory {
	private final ObjectMapper mapper;

	public ResourceIdentifier createIdentifier(String type, String id) {
		return new ResourceIdentifierImpl(type, id);
	}

	private ResourceData initializeData(ResourceIdentifier id, ResourceAttributes attributes) {
		var res = new ResourceDataImpl();
		res.setId(id.getId());
		res.setType(id.getType());
		res.setAttributes(attributes);
		ResourceRelationships relationships = createRelationships();
		ResourceMetadata metadata = createMetadata();
		ResourceLinks links = createLinks();
		res.setMetadata(metadata);
		res.setRelationships(relationships);
		res.setLinks(links);
		return res;
	}

	public ResourceData createData(ResourceIdentifier id, ResourceAttributes attributes) {
		ResourceData content = initializeData(id, attributes);
		return content;
	}

	public ResourceRelationships createRelationships() {
		return new ResourceRelationshipsImpl();
	}

	public RelationshipToOne createRelationshipToOne(ResourceIdentifier data) {
		return new RelationshipToOneImpl(data);
	}

	public RelationshipToOne createRelationshipToOne(
		ResourceIdentifier data, Map<String, String> links, Map<String, Object> metadata) {
		return new RelationshipToOneImpl(data, links, metadata);
	}

	public RelationshipToMany createRelationshipToMany() {
		return new RelationshipToManyImpl();
	}

	public RelationshipToMany createRelationshipToMany(List<ResourceIdentifier> data) {
		return new RelationshipToManyImpl(data);
	}

	public ResourceAttributes createAttributes(Object attributes) {
		@SuppressWarnings("unchecked")
		Map<String, Object> convertValue = mapper.convertValue(attributes, Map.class);
		return new ResourceAttributesImpl(convertValue);
	}

	public ResourceLinks createLinks() {
		return new ResourceLinksImpl();
	}

	public ResourceMetadata createMetadata() {
		return new ResourceMetadataImpl();
	}

	public List<ResourceIdentifier> getIdentifiers(List<ResourceData> data) {
		List<ResourceIdentifier> ids = new ArrayList<>();
		data.forEach(d -> ids.add(d));
		return ids;
	}
}
