package com.gray.bird.utils;

import java.time.LocalDateTime;
import java.util.UUID;

import com.gray.bird.post.ReplyType;
import com.gray.bird.post.dto.PostAttributes;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.post.dto.PostRelationships;
import com.gray.bird.post.dto.PostResource;
import com.gray.bird.post.dto.request.PostContentRequest;
import com.gray.bird.post.dto.request.PostRequest;

public class TestPostFactory {
	public static PostContentRequest postContentRequest() {
		return new PostContentRequest("testText", ReplyType.EVERYONE);
	}

	public static PostRequest postCreationRequestWithoutMedia() {
		return new PostRequest(postContentRequest());
	}

	public static PostProjection postProjection(Long postId, UUID userId) {
		return new PostProjection(
			postId, userId, "testText", false, false, ReplyType.EVERYONE, null, LocalDateTime.now());
	}

	public static PostResource postResource(Long postId, UUID userId) {
		return new PostResource(postId,
			new PostAttributes("testText", ReplyType.EVERYONE, LocalDateTime.now()),
			new PostRelationships(userId, null));
	}
}
