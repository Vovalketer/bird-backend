package com.gray.bird.postAggregator;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.gray.bird.common.jsonApi.ResourceFactory;
import com.gray.bird.common.jsonApi.ResourceMetadata;
import com.gray.bird.common.jsonApi.ResourceMetadataMapper;
import com.gray.bird.postAggregator.dto.PostInteractions;

@Component
@RequiredArgsConstructor
public class PostInteractionsMetadataMapper implements ResourceMetadataMapper<PostInteractions> {
	private final ResourceFactory resourceFactory;

	@Override
	public ResourceMetadata toResourceMetadata(PostInteractions data) {
		ResourceMetadata metadata = resourceFactory.createMetadata();
		metadata.addMetadata("repliesCount", data.repliesCount());
		metadata.addMetadata("likesCount", data.likesCount());
		metadata.addMetadata("repostsCount", data.repostsCount());
		return metadata;
	}
}
