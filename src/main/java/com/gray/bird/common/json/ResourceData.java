package com.gray.bird.common.json;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceData extends ResourceIdentifier {
	private ResourceAttributes attributes;
	private ResourceRelationships relationships;
	private ResourceLinks links;
	private ResourceMetadata metadata;

	public ResourceData(String type, String id, ResourceAttributes attributes,
		ResourceRelationships relationships, ResourceLinks links, ResourceMetadata metadata) {
		super(type, id);
		this.attributes = attributes;
		this.relationships = relationships;
		this.links = links;
		this.metadata = metadata;
	}

	public ResourceData(ResourceIdentifier id, ResourceAttributes attributes,
		ResourceRelationships relationships, ResourceLinks links, ResourceMetadata metadata) {
		super(id.getType(), id.getId());
		this.attributes = attributes;
		this.relationships = relationships;
		this.links = links;
		this.metadata = metadata;
	}

	public ResourceData(String type, String id, ResourceAttributes attributes) {
		super(type, id);
		this.attributes = attributes;
		this.relationships = new ResourceRelationships();
		this.links = new ResourceLinks();
		this.metadata = new ResourceMetadata();
	}

	public ResourceData() {
	}

	public void addRelationshipToMany(String key, RelationshipToMany relationship) {
		relationships.addRelationshipToMany(key, relationship);
	}

	public void addRelationshipToOne(String key, RelationshipToOne relationship) {
		relationships.addRelationshipToOne(key, relationship);
	}

	public Optional<RelationshipToMany> getRelationshipToMany(String key) {
		return relationships.getRelationshipToMany(key);
	}

	public Optional<RelationshipToOne> getRelationshipToOne(String key) {
		return relationships.getRelationshipToOne(key);
	}

	public Map<String, Object> getRelationships() {
		return relationships.getRelationships();
	}

	public void removeRelationship(String key) {
		relationships.removeRelationship(key);
	}

	public void addLink(String key, String url) {
		links.addLink(key, url);
	}

	public Optional<String> getLink(String key) {
		return links.getLink(key);
	}

	public void removeLink(String key) {
		links.removeLink(key);
	}

	public Object getAttribute(String key) {
		return attributes.getAttribute(key);
	}

	public <T> T getAttribute(String key, Class<T> type) {
		return type.cast(attributes.getAttribute(key));
	}

	public Map<String, Object> getAttributes() {
		return attributes.getAttributes();
	}

	public Map<String, String> getLinks() {
		return links.getLinks();
	}

	public Map<String, Object> getMetadata() {
		return metadata.getMetadata();
	}

	public Optional<Object> getMetadata(String type) {
		return metadata.getMetadata(type);
	}

	public <T> Optional<T> getMetadata(String type, Class<T> classType) {
		Object meta = metadata.getMetadata(type);
		if (meta != null) {
			return Optional.ofNullable(classType.cast(meta));
		} else {
			return Optional.empty();
		}
	}

	public void removeMetadata(String key) {
		metadata.removeMetadata(key);
	}

	public void addMetadata(String key, Object value) {
		metadata.addMetadata(key, value);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	public ResourceIdentifier getResourceIdentifier() {
		return new ResourceIdentifier(getType(), getId());
	}

	@Override
	public String toString() {
		return "ResourceData [type=" + super.getType() + "id=" + super.getId() + "attributes=" + attributes
			+ ", relationships=" + relationships + ", links=" + links + ", metadata=" + metadata + "]";
	}
}
