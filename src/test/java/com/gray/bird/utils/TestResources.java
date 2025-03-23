package com.gray.bird.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.gray.bird.post.ReplyType;
import com.gray.bird.post.dto.PostAttributes;
import com.gray.bird.post.dto.PostRelationships;
import com.gray.bird.post.dto.PostResource;
import com.gray.bird.user.dto.UserAttributes;
import com.gray.bird.user.dto.UserRelationships;
import com.gray.bird.user.dto.UserResource;

public class TestResources {
	public PostResource createPostResource(Long postId, UUID userId, Long parentPostId) {
		PostAttributes attributes = new PostAttributes(
			UUID.randomUUID().toString(), ReplyType.EVERYONE, LocalDateTime.now().minusDays(randomInt(1000)));
		PostRelationships reationships = new PostRelationships(userId, parentPostId);
		return new PostResource(postId, attributes, reationships);
	}

	public UserResource createUserResource(String userId) {
		UserAttributes attributes = new UserAttributes(UUID.randomUUID().toString(),
			UUID.randomUUID().toString(),
			UUID.randomUUID().toString(),
			LocalDate.now().minusDays(randomInt(100000)),
			UUID.randomUUID().toString(),
			"http://www.example.com/" + UUID.randomUUID().toString() + ".jpg",
			LocalDateTime.now().minusMinutes(randomInt(1000000)));
		UserRelationships relationships = new UserRelationships();
		return new UserResource(userId, attributes, relationships);
	}

	private Integer randomInt(int limit) {
		return ThreadLocalRandom.current().nextInt(limit);
	}
}
