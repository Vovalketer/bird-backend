package com.gray.bird.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
@Builder
@Getter
@AllArgsConstructor
public class PaginationMetadata {
	long totalElements;
	// int totalPages;
	// int pageNumber;
	// int size;
	int numberOfElements;
	boolean first;
	boolean last;
	boolean empty;
	Pageable pageable;
	Pageable nextPageable;
	Pageable previousPageable;

	// coupled with Spring boot
	public static PaginationMetadata fromPage(Page<?> page) {
		return PaginationMetadata.builder()
			.numberOfElements(page.getNumberOfElements())
			.pageable(page.getPageable())
			// int size = page.getSize();
			// Sort sort = page.getSort();
			.totalElements(page.getTotalElements())
			// int totalPages = page.getTotalPages();
			.empty(page.isEmpty())
			.first(page.isFirst())
			.last(page.isLast())
			.nextPageable(page.nextPageable())
			.previousPageable(page.previousPageable())
			.build();
	}
}
