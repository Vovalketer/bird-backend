package com.gray.bird.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
@AllArgsConstructor
public class PaginationMetadata implements Serializable {
	private long totalElements;
	private int numberOfElements;
	private boolean first;
	private boolean last;
	private boolean empty;
	private Pageable pageable;

	// coupled with Spring boot
	public static PaginationMetadata fromPage(Page<?> page) {
		return PaginationMetadata.builder()
			.numberOfElements(page.getNumberOfElements())
			.pageable(page.getPageable())
			.totalElements(page.getTotalElements())
			.empty(page.isEmpty())
			.first(page.isFirst())
			.last(page.isLast())
			.build();
	}
}
