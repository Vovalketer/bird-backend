package com.gray.bird.common.jsonApi;

public class ResourcePaginationMetadataImpl implements ResourcePaginationMetadata {
	private long totalElements;
	private int numberOfElements;
	private boolean first;
	private boolean last;
	private boolean empty;

	public ResourcePaginationMetadataImpl(
		long totalElements, int numberOfElements, boolean first, boolean last, boolean empty) {
		this.totalElements = totalElements;
		this.numberOfElements = numberOfElements;
		this.first = first;
		this.last = last;
		this.empty = empty;
	}

	@Override
	public long getTotalElements() {
		return totalElements;
	}

	@Override
	public int getNumberOfElements() {
		return numberOfElements;
	}

	@Override
	public boolean isFirst() {
		return first;
	}

	@Override
	public boolean isLast() {
		return last;
	}

	@Override
	public boolean isEmpty() {
		return empty;
	}

	@Override
	public String toString() {
		return "ResourcePaginationMetadataImpl [totalElements=" + totalElements + ", numberOfElements="
			+ numberOfElements + ", first=" + first + ", last=" + last + ", empty=" + empty + "]";
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public void setNumberOfElements(int numberOfElements) {
		this.numberOfElements = numberOfElements;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
}
