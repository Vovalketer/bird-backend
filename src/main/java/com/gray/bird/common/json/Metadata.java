package com.gray.bird.common.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Metadata {
	private Map<String, Object> metadata;

	public Metadata(Map<String, Object> meta) {
		if (meta == null) {
			meta = new HashMap<>();
		}
		this.metadata = meta;
	}

	public Metadata() {
		metadata = new HashMap<>();
	}

	@JsonAnyGetter
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
