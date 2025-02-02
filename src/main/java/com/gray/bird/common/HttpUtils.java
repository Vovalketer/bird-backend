package com.gray.bird.common;

import jakarta.servlet.http.Cookie;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class HttpUtils {
	public static Optional<Cookie> extractCookie(Cookie[] cookies, String name) {
		if (cookies != null) {
			return Arrays.stream(cookies)
				.filter(cookie -> Objects.equals(cookie.getName(), name))
				.filter(cookie -> cookie.getValue() != "")
				.findAny();
		}
		return Optional.empty();
	}

	public static Cookie createCookie(String name, String value) {
		if (name != null && value != null) {
			return new Cookie(name, value);
		}
		throw new InvalidParameterException("Attempted to create a cookie with a null value");
	}

	public static Cookie expireCookie(Cookie cookie) {
		cookie.setMaxAge(0);
		return cookie;
	}
}
