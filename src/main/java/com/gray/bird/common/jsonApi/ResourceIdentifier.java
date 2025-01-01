package com.gray.bird.common.jsonApi;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface ResourceIdentifier {
	String getType();

	String getId();

	default boolean idIsEqualTo(ResourceIdentifier other) {
		if (other == null) {
			return false;
		}
		return this.getType().equals(other.getType()) && this.getId().equals(other.getId());
	}
}
