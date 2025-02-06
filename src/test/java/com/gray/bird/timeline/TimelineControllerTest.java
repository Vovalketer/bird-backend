package com.gray.bird.timeline;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import com.gray.bird.common.JsonApiResponse;
import com.gray.bird.common.PaginationMetadata;
import com.gray.bird.common.utils.JsonApiResponseFactory;
import com.gray.bird.common.utils.MetadataUtils;
import com.gray.bird.post.dto.PostResource;
import com.gray.bird.postAggregator.PostAggregate;
import com.gray.bird.postAggregator.PostAggregateResourceMapper;
import com.gray.bird.postAggregator.PostAggregatorService;
import com.gray.bird.timeline.dto.TimelineEntryDto;
import com.gray.bird.user.UserService;
import com.gray.bird.utils.TestResources;
import com.gray.bird.utils.TestUtils;

@ExtendWith(SpringExtension.class)
public class TimelineControllerTest {
	@Mock
	private TimelineService timelineService;
	@Mock
	private PostAggregatorService postAggregatorService;
	@Mock
	private UserService userService;
	@Mock
	private MetadataUtils metadataUtils;
	@Mock
	private JsonApiResponseFactory responseFactory;
	@Mock
	private PostAggregateResourceMapper postAggregateResourceMapper;
	@InjectMocks
	private TimelineController timelineController;
	private TestUtils testUtils = new TestUtils();
	private TestResources testResources = new TestResources();
}
