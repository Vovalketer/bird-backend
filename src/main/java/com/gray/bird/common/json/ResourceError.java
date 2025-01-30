package com.gray.bird.common.json;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResourceError {
	private String id; // uuid
	private String status;
	private String code; // application error code
	private String title;
	private String detail;
	private ResourceErrorSource source;
	private Metadata metadata;
}
