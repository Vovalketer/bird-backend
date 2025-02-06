package com.gray.bird.timeline;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gray.bird.common.JsonApiResponse;
import com.gray.bird.common.PaginationMetadata;
import com.gray.bird.common.ResourcePaths;
import com.gray.bird.common.utils.JsonApiResponseFactory;
import com.gray.bird.common.utils.MetadataUtils;
import com.gray.bird.post.dto.PostResource;
import com.gray.bird.postAggregator.PostAggregate;
import com.gray.bird.postAggregator.PostAggregateResourceMapper;
import com.gray.bird.postAggregator.PostAggregatorService;
import com.gray.bird.timeline.dto.TimelineEntryDto;
import com.gray.bird.user.UserService;

@RestController
@RequestMapping(ResourcePaths.USERS_USERNAME_TIMELINES)
@RequiredArgsConstructor
public class TimelineController {
	private final TimelineService timelineService;
	private final PostAggregatorService postAggregatorService;
	private final UserService userService;
	private final MetadataUtils metadataUtils;
	private final JsonApiResponseFactory responseFactory;
	private final PostAggregateResourceMapper postAggregateResourceMapper;

	@GetMapping("/following")
	public ResponseEntity<?> getFollowingTimeline(@RequestParam String username) {
		// following, AUTH REQUIRED
		return null;
	}
}
