package com.gray.bird.common;

import java.util.Collections;
import java.util.Map;

public class HttpResponseData<T> {
	Map<String, T> content;
	public HttpResponseData(String fieldName, T content) {
		this.content = Collections.singletonMap(fieldName, content);
	}
}
