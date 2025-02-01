package com.gray.bird.common.json;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@AllArgsConstructor
@Getter
public class ResourceErrorResponse {
	Collection<ResourceError> errors;
}
