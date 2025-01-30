package com.gray.bird.common.json;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResourceErrorSource {
	private String pointer;
	private String parameter;
	private String header;
}
