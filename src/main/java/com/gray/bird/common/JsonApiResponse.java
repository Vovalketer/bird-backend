package com.gray.bird.common;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gray.bird.common.json.ResourceResponse;
import com.gray.bird.media.dto.MediaResource;
import com.gray.bird.post.dto.PostResource;
import com.gray.bird.user.dto.UserResource;

public class JsonApiResponse<T> extends ResourceResponse<T, ResourceIncluded> {
	@JsonCreator
	public JsonApiResponse(T data, ResourceIncluded included) {
		super(data, included);
	}

	public JsonApiResponse(T data) {
		super(data, new ResourceIncluded());
	}

	public void includeUser(UserResource user) {
		super.getIncluded().addUser(user);
	}

	public void includePost(PostResource post) {
		super.getIncluded().addPost(post);
	}

	public void includeMedia(MediaResource media) {
		super.getIncluded().addMedia(media);
	}

	public void includeAllUsers(Collection<UserResource> users) {
		super.getIncluded().addAllUsers(users);
	}

	public void includeAllPosts(Collection<PostResource> posts) {
		super.getIncluded().addAllPosts(posts);
	}

	public void includeAllMedia(Collection<MediaResource> media) {
		super.getIncluded().addAllMedia(media);
	}
}
