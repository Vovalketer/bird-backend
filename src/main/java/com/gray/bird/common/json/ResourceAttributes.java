package com.gray.bird.common.json;

import java.util.HashMap;
import java.util.Map;

public class ResourceAttributes {
	private Map<String, Object> attributes;

	public ResourceAttributes(Map<String, Object> attributes) {
		if (attributes == null) {
			attributes = new HashMap<>();
		}
		this.attributes = attributes;
	}

	public ResourceAttributes() {
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	public <T> T getAttribute(String key, Class<T> type) {
		return type.cast(attributes.get(key));
	}

	@Override
	public String toString() {
		return "ResourceAttributes [attributes=" + attributes + "]";
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
}
