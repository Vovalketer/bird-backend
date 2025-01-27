package com.gray.bird.common.json;

public interface ResourceMetadataMapper<T> {
	ResourceMetadata toResourceMetadata(T data);
}
