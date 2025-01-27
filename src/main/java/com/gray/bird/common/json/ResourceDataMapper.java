package com.gray.bird.common.json;

public interface ResourceDataMapper<T> {
	ResourceData toResource(T data);
}
