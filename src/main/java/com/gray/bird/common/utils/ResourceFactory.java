package com.gray.bird.common.utils;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gray.bird.common.jsonApi.Relationship;
import com.gray.bird.common.jsonApi.RelationshipImpl;
import com.gray.bird.common.jsonApi.ResourceAttributes;
import com.gray.bird.common.jsonApi.ResourceAttributesImpl;
import com.gray.bird.common.jsonApi.ResourceCollectionAggregate;
import com.gray.bird.common.jsonApi.ResourceCollectionAggregateImpl;
import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceDataImpl;
import com.gray.bird.common.jsonApi.ResourceIdentifier;
import com.gray.bird.common.jsonApi.ResourceIdentifierImpl;
import com.gray.bird.common.jsonApi.ResourceLinks;
import com.gray.bird.common.jsonApi.ResourceLinksImpl;
import com.gray.bird.common.jsonApi.ResourceMetadata;
import com.gray.bird.common.jsonApi.ResourceMetadataImpl;
import com.gray.bird.common.jsonApi.ResourceRelationships;
import com.gray.bird.common.jsonApi.ResourceRelationshipsImpl;
import com.gray.bird.common.jsonApi.ResourceSingleAggregate;
import com.gray.bird.common.jsonApi.ResourceSingleAggregateImpl;

@Component
@RequiredArgsConstructor
public class ResourceFactory {
	private final ObjectMapper mapper;

	private ResourceSingleAggregate initializeAggregate(ResourceData data) {
		ResourceMetadata metadata = createMetadata();
		ResourceSingleAggregateImpl aggregate = new ResourceSingleAggregateImpl(data, metadata);

		return aggregate;
	}

	private ResourceCollectionAggregate initializeAggregate(List<ResourceData> data) {
		List<ResourceData> included = new ArrayList<>();
		ResourceMetadata metadata = createMetadata();
		ResourceCollectionAggregateImpl aggregate =
			new ResourceCollectionAggregateImpl(data, included, metadata);
		return aggregate;
	}

	private ResourceCollectionAggregate initializeAggregate() {
		List<ResourceData> data = new ArrayList<>();
		List<ResourceData> included = new ArrayList<>();
		ResourceMetadata metadata = createMetadata();
		ResourceCollectionAggregateImpl aggregate =
			new ResourceCollectionAggregateImpl(data, included, metadata);
		return aggregate;
	}

	public ResourceSingleAggregate createSingleAggregate(ResourceData data) {
		ResourceSingleAggregate aggregate = initializeAggregate(data);
		return aggregate;
	}

	public ResourceSingleAggregate createSingleAggregate(
		ResourceData data, List<ResourceData> included, ResourceMetadata meta) {
		return new ResourceSingleAggregateImpl(data, included, meta);
	}

	public ResourceCollectionAggregate createCollectionAggregate() {
		return initializeAggregate();
	}

	public ResourceCollectionAggregate createCollectionAggregate(List<ResourceData> data) {
		return initializeAggregate(data);
	}

	public ResourceCollectionAggregate createCollectionAggregate(
		List<ResourceData> data, List<ResourceData> included, ResourceMetadata meta) {
		return new ResourceCollectionAggregateImpl(data, included, meta);
	}

	public ResourceIdentifier createIdentifier(String type, String id) {
		return new ResourceIdentifierImpl(type, id);
	}

	private ResourceData initializeContent(ResourceIdentifier id, ResourceAttributes attributes) {
		var res = new ResourceDataImpl();
		res.setId(id.getId());
		res.setType(id.getType());
		res.setAttributes(attributes);
		ResourceRelationships relationships = createRelationships();
		ResourceMetadata metadata = createMetadata();
		ResourceLinks links = createLinks();
		res.setMeta(metadata);
		res.setRelationships(relationships);
		res.setLinks(links);
		return res;
	}

	public ResourceData createContent(ResourceIdentifier id, ResourceAttributes attributes) {
		ResourceData content = initializeContent(id, attributes);
		return content;
	}

	public ResourceData createContent(ResourceIdentifier id, ResourceAttributes attributes,
		ResourceRelationships relationships, ResourceLinks links, ResourceMetadata meta) {
		return new ResourceDataImpl(id, attributes, relationships, links, meta);
	}

	public ResourceRelationships createRelationships() {
		return new ResourceRelationshipsImpl();
	}

	public ResourceRelationships createRelationships(Map<String, List<Relationship>> relationships) {
		return new ResourceRelationshipsImpl(relationships);
	}

	public Relationship createRelationship(ResourceIdentifier data) {
		return new RelationshipImpl(data);
	}

	public Relationship createRelationship(List<ResourceIdentifier> data) {
		return new RelationshipImpl(data);
	}

	public Relationship createRelationship(ResourceIdentifier data, Map<String, String> links) {
		return new RelationshipImpl(data, links);
	}

	public Relationship createRelationship(List<ResourceIdentifier> data, Map<String, String> links) {
		return new RelationshipImpl(data, links);
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
}
