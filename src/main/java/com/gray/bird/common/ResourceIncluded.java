package com.gray.bird.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.gray.bird.media.dto.MediaResource;
import com.gray.bird.post.dto.PostResource;
import com.gray.bird.user.dto.UserResource;

@AllArgsConstructor
@Getter
public class ResourceIncluded {
	List<UserResource> users;
	List<PostResource> posts;
	List<MediaResource> media;

	public ResourceIncluded() {
		this.users = new ArrayList<>();
		this.posts = new ArrayList<>();
		this.media = new ArrayList<>();
	}

	public void addUser(UserResource user) {
		this.users.add(user);
	}

	public void addPost(PostResource post) {
		this.posts.add(post);
	}

	public void addMedia(MediaResource media) {
		this.media.add(media);
	}

	public void addAllUsers(Collection<UserResource> users) {
		this.users.addAll(users);
	}

	public void addAllPosts(Collection<PostResource> posts) {
		this.posts.addAll(posts);
	}

	public void addAllMedia(Collection<MediaResource> media) {
		this.media.addAll(media);
	}
}
