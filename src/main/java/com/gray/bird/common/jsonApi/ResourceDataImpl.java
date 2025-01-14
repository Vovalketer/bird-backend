package com.gray.bird.common.jsonApi;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceDataImpl implements ResourceData {
	private String type;
	private String id;
	private ResourceAttributes attributes;
	private ResourceRelationships relationships;
	private ResourceLinks links;
	private ResourceMetadata metadata;

	public ResourceDataImpl() {
	}

	public ResourceDataImpl(ResourceIdentifier id, ResourceAttributes attributes,
		ResourceRelationships relationships, ResourceLinks links, ResourceMetadata meta) {
		this.type = id.getType();
		this.id = id.getId();
		this.attributes = attributes;
		this.relationships = relationships;
		this.links = links;
		this.metadata = meta;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void addRelationshipToMany(String key, RelationshipToMany relationship) {
		relationships.addRelationshipToMany(key, relationship);
	}

	@Override
	public void addRelationshipToOne(String key, RelationshipToOne relationship) {
		relationships.addRelationshipToOne(key, relationship);
	}

	@Override
	public Optional<RelationshipToMany> getRelationshipToMany(String key) {
		return relationships.getRelationshipToMany(key);
	}

	@Override
	public Optional<RelationshipToOne> getRelationshipToOne(String key) {
		return relationships.getRelationshipToOne(key);
	}

	@Override
	public Map<String, Object> getRelationships() {
		return relationships.getRelationships();
	}

	@Override
	public void removeRelationship(String key) {
		relationships.removeRelationship(key);
	}

	@Override
	public void addLink(String key, String url) {
		links.addLink(key, url);
	}

	@Override
	public Optional<String> getLink(String key) {
		return links.getLink(key);
	}

	@Override
	public void removeLink(String key) {
		links.removeLink(key);
	}

	@Override
	public Object getAttribute(String key) {
		return attributes.getAttribute(key);
	}

	@Override
	public <T> T getAttribute(String key, Class<T> type) {
		return getAttribute(key, type);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes.getAttributes();
	}

	@Override
	public Map<String, String> getLinks() {
		return links.getLinks();
	}

	@Override
	public Map<String, Object> getMetadata() {
		return metadata.getMetadata();
	}

	@Override
	public Optional<Object> getMetadata(String type) {
		return metadata.getMetadata(type);
	}

	@Override
	public <T> Optional<T> getMetadata(String type, Class<T> classType) {
		Object meta = metadata.getMetadata(type);
		if (meta != null) {
			return Optional.ofNullable(classType.cast(meta));
		} else {
			return Optional.empty();
		}
	}

	@Override
	public void removeMetadata(String key) {
		metadata.removeMetadata(key);
	}

	@Override
	public void addMetadata(String key, Object value) {
		metadata.addMetadata(key, value);
	}

	@Override
	public String toString() {
		return "ResourceContentImpl [type=" + type + ", id=" + id + ", attributes=" + attributes
			+ ", relationships=" + relationships + ", links=" + links + ", meta=" + metadata + "]";
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setAttributes(ResourceAttributes attributes) {
		this.attributes = attributes;
	}

	public void setRelationships(ResourceRelationships relationships) {
		this.relationships = relationships;
	}

	public void setLinks(ResourceLinks links) {
		this.links = links;
	}

	public void setMetadata(ResourceMetadata meta) {
		this.metadata = meta;
	}
}
