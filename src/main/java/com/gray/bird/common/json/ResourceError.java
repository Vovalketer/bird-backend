package com.gray.bird.common.json;

public class ResourceError {
	private String id; // uuid
	private String status;
	private String code; // application error code
	private String title;
	private String detail;
	private ResourceErrorSource source;
	private ResourceMetadata metadata;

	public ResourceError(String id, String status, String code, String title, String detail,
		ResourceErrorSource source, ResourceMetadata metadata) {
		this.id = id;
		this.status = status;
		this.code = code;
		this.title = title;
		this.detail = detail;
		this.source = source;
		this.metadata = metadata;
	}

	public String getId() {
		return id;
	}

	public String getStatus() {
		return status;
	}

	public String getCode() {
		return code;
	}

	public String getTitle() {
		return title;
	}

	public String getDetail() {
		return detail;
	}

	public ResourceErrorSource getSource() {
		return source;
	}

	public ResourceMetadata getMetadata() {
		return metadata;
	}
}
