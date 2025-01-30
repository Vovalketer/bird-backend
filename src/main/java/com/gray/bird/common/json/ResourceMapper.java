package com.gray.bird.common.json;

public interface ResourceMapper<T, U extends ResourceData<?, ?, ?>> {
	U toResource(T entity);
}
