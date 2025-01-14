package com.gray.bird.common.jsonApi;

public interface ResourceDataMapper<T> {
	ResourceData toResource(T data);
}
