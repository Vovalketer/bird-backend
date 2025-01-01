package com.gray.bird.common;

public class RequestContext {
	private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

	private RequestContext() {
	}

	public static void reset() {
		// setting to null isnt recommended because of the possible memory leaks
		// use remove instead
		USER_ID.remove();
	}

	public static void setUserId(Long userId) {
		USER_ID.set(userId);
	}

	public static Long getUserId() {
		return USER_ID.get();
	}
}
