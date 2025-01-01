package com.gray.bird.common.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gray.bird.common.PaginationMetadata;

public class PaginationUtils {
	public static <T> PaginationMetadata extractPaginationMetadata(Page<T> page) {
		// int number = page.getNumber();
		int numberOfElements = page.getNumberOfElements();
		Pageable pageable = page.getPageable();
		// int size = page.getSize();
		// Sort sort = page.getSort();
		long totalElements = page.getTotalElements();
		// int totalPages = page.getTotalPages();
		boolean empty = page.isEmpty();
		boolean first = page.isFirst();
		boolean last = page.isLast();
		Pageable nextPageable = page.nextPageable();
		Pageable previousPageable = page.previousPageable();

		return PaginationMetadata
			.builder()
			// .pageNumber(number)
			.numberOfElements(numberOfElements)
			.pageable(pageable)
			// .size(size)
			.totalElements(totalElements)
			// .totalPages(totalPages)
			.empty(empty)
			.first(first)
			.last(last)
			.nextPageable(nextPageable)
			.previousPageable(previousPageable)
			.build();
	}
}
