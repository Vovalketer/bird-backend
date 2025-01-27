package com.gray.bird.common.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ResourceMetadata {
	private Map<String, Object> metadata;

	public ResourceMetadata(Map<String, Object> meta) {
		if (meta == null) {
			meta = new HashMap<>();
		}
		this.metadata = meta;
	}

	public ResourceMetadata() {
		metadata = new HashMap<>();
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public Optional<Object> getMetadata(String key) {
		return Optional.ofNullable(metadata.get(key));
	}

	public void addMetadata(String key, Object value) {
		metadata.put(key, value);
	}

	public void removeMetadata(String key) {
		metadata.remove(key);
	}

	public String toString() {
		return "ResourceMetadata [meta=" + metadata + "]";
	}

	public <T> Optional<T> getMetadata(String key, Class<T> type) {
		Object object = metadata.get(key);
		if (object != null) {
			return Optional.ofNullable(type.cast(object));
		} else {
			return Optional.empty();
		}
	}

	public void setMetadata(Map<String, Object> meta) {
		this.metadata = meta;
	}
}
