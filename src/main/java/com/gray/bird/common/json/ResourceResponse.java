package com.gray.bird.common.json;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResourceResponse<T, U> {
	private T data; // cant narrow the type down without losing the ability to use lists
	private U included;
	private Metadata metadata = new Metadata();
	private Links links = new Links();

	public ResourceResponse(T data, U included) {
		this.data = data;
		this.included = included;
	}

	public void addMetadata(String key, Object metadata) {
		this.metadata.addMetadata(key, metadata);
	}
}
