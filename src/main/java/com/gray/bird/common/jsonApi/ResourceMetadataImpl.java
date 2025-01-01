package com.gray.bird.common.jsonApi;

import java.util.HashMap;
import java.util.Map;

public class ResourceMetadataImpl implements ResourceMetadata {
	private Map<String, Object> meta;

	public ResourceMetadataImpl(Map<String, Object> meta) {
		this.meta = meta;
	}

	public ResourceMetadataImpl() {
		meta = new HashMap<>();
	}

	@Override
	public Map<String, Object> getMetadata() {
		return meta;
	}

	@Override
	public Object getMetadata(String name) {
		return meta.get(name);
	}

	@Override
	public void addMetadata(String name, Object value) {
		meta.put(name, value);
	}

	@Override
	public void removeMetadata(String name) {
		meta.remove(name);
	}

	@Override
	public String toString() {
		return "ResourceMetadataImpl [meta=" + meta + "]";
	}

	@Override
	public <T> T getMetadata(String key, Class<T> type) {
		Object object = meta.get(key);
		return type.cast(object);
	}
}
