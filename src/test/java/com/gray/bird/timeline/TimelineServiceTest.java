package com.gray.bird.timeline;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.UUID;

import com.gray.bird.timeline.dto.TimelineEntryDto;

@SpringJUnitConfig
public class TimelineServiceTest {
	@Mock
	private TimelineRepository repository;
	@InjectMocks
	private TimelineService service;

	@Test
	void testAddEntry() {
		UUID userId = UUID.randomUUID();
		Long postId = 1L;
		TimelineEntity entity = new TimelineEntity(userId, postId);
		Mockito.when(repository.save(Mockito.any(TimelineEntity.class))).thenReturn(entity);

		service.addEntry(userId, postId);

		Mockito.verify(repository).save(Mockito.any(TimelineEntity.class));
	}

	@Test
	void testDeleteEntry() {
		UUID userId = UUID.randomUUID();
		Long postId = 1L;
		Mockito.doNothing().when(repository).delete(Mockito.any(TimelineEntity.class));

		service.deleteEntry(userId, postId);

		Mockito.verify(repository).delete(Mockito.any(TimelineEntity.class));
	}

	@Test
	void testGetHomeTimeline() {
		UUID userId = UUID.randomUUID();
		Pageable pageable = PageRequest.of(0, 10);
		@SuppressWarnings("unchecked")
		Page<TimelineEntryDto> page = Mockito.mock(Page.class);
		Mockito.when(repository.findByUserId(userId, pageable)).thenReturn(page);

		Page<TimelineEntryDto> homeTimeline = service.getHomeTimeline(userId, pageable);

		Assertions.assertThat(homeTimeline).isEqualTo(page);
		Mockito.verify(repository).findByUserId(userId, pageable);
	}
}
