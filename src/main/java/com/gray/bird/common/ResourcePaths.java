package com.gray.bird.common;

public class ResourcePaths {
	public static final String HOST = "http://localhost:8080";
	public static final String BASE = "/api";
	public static final String POSTS = BASE + "/posts";
	public static final String POSTS_POSTID_LIKES = POSTS + "/{postId}/likes";
	public static final String POSTS_POSTID_REPOSTS = POSTS + "/{postId}/reposts";
	public static final String POSTS_POSTID_REPLIES = POSTS + "/{postId}/replies";

	public static final String AUTH = BASE + "/auth";
	public static final String USERS = BASE + "/users";
	public static final String USERS_USERNAME_POSTS = USERS + "/{username}/posts";
	public static final String USERS_USERNAME_TIMELINES = USERS + "/{username}/timelines";
}
