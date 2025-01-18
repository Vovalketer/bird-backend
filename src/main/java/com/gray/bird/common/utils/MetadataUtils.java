package com.gray.bird.common.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.gray.bird.common.PaginationMetadata;

@Component
public class MetadataUtils {
	public <T> PaginationMetadata extractPaginationMetadata(Page<T> page) {
		int numberOfElements = page.getNumberOfElements();
		Pageable pageable = page.getPageable();
		long totalElements = page.getTotalElements();
		boolean empty = page.isEmpty();
		boolean first = page.isFirst();
		boolean last = page.isLast();

		return PaginationMetadata.builder()
			.numberOfElements(numberOfElements)
			.pageable(pageable)
			.totalElements(totalElements)
			.empty(empty)
			.first(first)
			.last(last)
			.build();
	}
}
