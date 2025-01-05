package com.gray.bird.common.jsonApi;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
	public Optional<Object> getMetadata(String key) {
		return Optional.ofNullable(meta.get(key));
	}

	@Override
	public void addMetadata(String key, Object value) {
		meta.put(key, value);
	}

	@Override
	public void removeMetadata(String key) {
		meta.remove(key);
	}

	@Override
	public String toString() {
		return "ResourceMetadataImpl [meta=" + meta + "]";
	}

	@Override
	public <T> Optional<T> getMetadata(String key, Class<T> type) {
		Object object = meta.get(key);
		if (object != null) {
			return Optional.ofNullable(type.cast(object));
		} else {
			return Optional.empty();
		}
	}

	public void setMeta(Map<String, Object> meta) {
		this.meta = meta;
	}
}
