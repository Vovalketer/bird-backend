package com.gray.bird.common;

public class ResourcePaths {
	public static final String BASE = "/api";
	public static final String POSTS = BASE + "/posts";
	public static final String LIKES = POSTS + "/{postId}/likes";
	public static final String RETWEETS = POSTS + "/{postId}/reposts";
	public static final String REPLIES = POSTS + "/{postId}/replies";

	public static final String AUTH = BASE + "/auth";
	public static final String USERS = BASE + "/users";
}
