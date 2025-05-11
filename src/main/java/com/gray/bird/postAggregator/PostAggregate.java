package com.gray.bird.postAggregator;

import java.util.List;

import com.gray.bird.media.dto.MediaDto;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.postAggregator.dto.PostEngagement;

public record PostAggregate(PostProjection post, List<MediaDto> media, PostEngagement engagement) {
}
