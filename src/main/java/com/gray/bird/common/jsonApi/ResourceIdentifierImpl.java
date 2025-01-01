package com.gray.bird.common.jsonApi;

public class ResourceIdentifierImpl implements ResourceIdentifier {
	private String type;
	private String id;

	public ResourceIdentifierImpl(String type, String id) {
		this.type = type;
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "ResourceIdentifierImpl [type=" + type + ", id=" + id + "]";
	}
}
