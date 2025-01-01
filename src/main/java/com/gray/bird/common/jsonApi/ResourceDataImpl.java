package com.gray.bird.common.jsonApi;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceDataImpl implements ResourceData {
	private String type;
	private String id;
	private ResourceAttributes attributes;
	private ResourceRelationships relationships;
	private ResourceLinks links;
	private ResourceMetadata meta;

	public ResourceDataImpl() {
	}

	public ResourceDataImpl(ResourceIdentifier id, ResourceAttributes attributes,
		ResourceRelationships relationships, ResourceLinks links, ResourceMetadata meta) {
		this.type = id.getType();
		this.id = id.getId();
		this.attributes = attributes;
		this.relationships = relationships;
		this.links = links;
		this.meta = meta;
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
	public Map<String, List<Relationship>> getRelationships() {
		return relationships.getRelationships();
	}

	@Override
	public List<Relationship> getRelationshipsForType(String type) {
		return relationships.getRelationshipsForType(type);
	}

	@Override
	public void addRelationship(String name, Relationship relationship) {
		relationships.addRelationship(name, relationship);
	}

	@Override
	public void removeRelationship(String name) {
		relationships.removeRelationship(name);
	}

	@Override
	public void addLink(String name, String url) {
		links.addLink(name, url);
	}

	@Override
	public String getLink(String name) {
		return links.getLink(name);
	}

	@Override
	public void removeLink(String name) {
		links.removeLink(name);
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.getAttribute(name);
	}

	@Override
	public <T> T getAttribute(String name, Class<T> type) {
		return getAttribute(name, type);
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
		return meta.getMetadata();
	}

	@Override
	public Object getMetadata(String key) {
		return meta.getMetadata(key);
	}

	@Override
	public <T> T getMetadata(String key, Class<T> type) {
		Object metadata = meta.getMetadata(key);
		return type.cast(metadata);
	}

	@Override
	public void removeMetadata(String key) {
		meta.removeMetadata(key);
	}

	@Override
	public void addMetadata(String name, Object value) {
		meta.addMetadata(name, value);
	}

	@Override
	public String toString() {
		return "ResourceContentImpl [type=" + type + ", id=" + id + ", attributes=" + attributes
			+ ", relationships=" + relationships + ", links=" + links + ", meta=" + meta + "]";
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

	public void setMeta(ResourceMetadata meta) {
		this.meta = meta;
	}
}
