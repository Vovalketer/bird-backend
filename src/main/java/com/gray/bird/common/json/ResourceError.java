package com.gray.bird.common.json;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonInclude;

@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResourceError {
	// private String id; // uuid
	private String status;
	// private String code; // application error code
	private String title;
	private String detail;
	private ResourceErrorSource source;
	private Metadata metadata;

	public ResourceError(String status, String title, String detail, ResourceErrorSource source) {
		this.status = status;
		this.title = title;
		this.detail = detail;
		this.source = source;
	}

	public ResourceError(String status, String title, String detail) {
		this.status = status;
		this.title = title;
		this.detail = detail;
	}

	public void addMetadata(String key, Object value) {
		if (this.metadata == null) {
			this.metadata = new Metadata();
		}
		this.metadata.addMetadata(key, value);
	}
}
