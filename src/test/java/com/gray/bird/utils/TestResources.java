package com.gray.bird.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.gray.bird.common.jsonApi.ResourceAttributesImpl;
import com.gray.bird.common.jsonApi.ResourceCollectionAggregate;
import com.gray.bird.common.jsonApi.ResourceCollectionAggregateImpl;
import com.gray.bird.common.jsonApi.ResourceData;
import com.gray.bird.common.jsonApi.ResourceDataImpl;
import com.gray.bird.common.jsonApi.ResourceIdentifierImpl;
import com.gray.bird.common.jsonApi.ResourceLinksImpl;
import com.gray.bird.common.jsonApi.ResourceMetadataImpl;
import com.gray.bird.common.jsonApi.ResourceRelationshipsImpl;
import com.gray.bird.common.jsonApi.ResourceSingleAggregate;
import com.gray.bird.common.jsonApi.ResourceSingleAggregateImpl;

public class TestResources {
	public ResourceData createPostResourceData() {
		ResourceIdentifierImpl id = new ResourceIdentifierImpl("post", randomLong().toString());
		Map<String, Object> att = Map.of("text",
			UUID.randomUUID().toString(),
			"replyType",
			"EVERYONE",
			"created_at",
			LocalDateTime.now().minusSeconds(randomInt()));
		ResourceAttributesImpl attributes = new ResourceAttributesImpl(att);
		ResourceRelationshipsImpl relationships = new ResourceRelationshipsImpl();
		ResourceLinksImpl links = new ResourceLinksImpl();
		ResourceMetadataImpl meta = new ResourceMetadataImpl();
		return new ResourceDataImpl(id, attributes, relationships, links, meta);
	}

	public List<ResourceData> createPostResourceData(int size) {
		List<ResourceData> list = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			list.add(createPostResourceData());
		}
		return list;
	}

	public ResourceSingleAggregate createPostSingleAggregate() {
		return new ResourceSingleAggregateImpl(createPostResourceData());
	}

	public ResourceCollectionAggregate createPostCollectionAggregate(int size) {
		return new ResourceCollectionAggregateImpl(createPostResourceData(size));
	}

	public ResourceSingleAggregate createUserSingleAggregate() {
		return new ResourceSingleAggregateImpl(createUserResourceData());
	}

	private ResourceData createUserResourceData() {
		ResourceIdentifierImpl id = new ResourceIdentifierImpl("user", randomLong().toString());
		Map<String, Object> att = Map.of("username",
			UUID.randomUUID().toString(),
			"handle",
			UUID.randomUUID().toString(),
			"bio",
			UUID.randomUUID().toString(),
			"dateOfBirth",
			LocalDateTime.now().minusSeconds(randomInt()),
			"location",
			UUID.randomUUID().toString(),
			"profileImage",
			UUID.randomUUID().toString(),
			"createdAt",
			LocalDateTime.now().minusSeconds(randomInt()));
		ResourceAttributesImpl attributes = new ResourceAttributesImpl(att);
		ResourceRelationshipsImpl relationships = new ResourceRelationshipsImpl();
		ResourceLinksImpl links = new ResourceLinksImpl();
		ResourceMetadataImpl meta = new ResourceMetadataImpl();
		return new ResourceDataImpl(id, attributes, relationships, links, meta);
	}

	private Long randomLong() {
		long nextLong = ThreadLocalRandom.current().nextLong();
		Long number = nextLong;
		return number;
	}

	private Integer randomInt() {
		int nextInt = ThreadLocalRandom.current().nextInt();
		Integer integer = nextInt;
		return integer;
	}
}
