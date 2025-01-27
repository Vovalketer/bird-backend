package com.gray.bird.common.json;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ResourcePaginationMetadata {
	private long totalElements;
	private int numberOfElements;
	private boolean first;
	private boolean last;
	private boolean empty;

	public ResourcePaginationMetadata(
		long totalElements, int numberOfElements, boolean first, boolean last, boolean empty) {
		this.totalElements = totalElements;
		this.numberOfElements = numberOfElements;
		this.first = first;
		this.last = last;
		this.empty = empty;
	}
}
