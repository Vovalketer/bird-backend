package com.gray.bird.common.jsonApi;

import java.util.Map;

public class ResourceAttributesImpl implements ResourceAttributes {
	private Map<String, Object> attributes;

	public ResourceAttributesImpl(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public <T> T getAttribute(String name, Class<T> type) {
		return type.cast(attributes.get(name));
	}

	@Override
	public String toString() {
		return "ResourceAttributesImpl [attributes=" + attributes + "]";
	}
}
