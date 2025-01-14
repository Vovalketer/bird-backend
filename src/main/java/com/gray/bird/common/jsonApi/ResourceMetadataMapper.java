package com.gray.bird.common.jsonApi;

public interface ResourceMetadataMapper<T> {
	ResourceMetadata toResourceMetadata(T data);
}
