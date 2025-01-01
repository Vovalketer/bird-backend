package com.gray.bird.common;

import org.springframework.http.HttpStatus;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.security.InvalidParameterException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HttpUtils {
	/**
	 * <p>
	 * This will return an object that will get serialized as a JSON with the following shape:
	 * </p>
	 * <ul>
	 * <li>
	 * <code>time</code>: string form
	 * </li>
	 * <code>data</code>: map of the data
	 * <code>code</code>: status code, integer
	 * <code>status</code>: status code, string
	 * <code>message</code>: response message
	 * <code>exception</code>: exception message, if applicable
	 * <code>path</code>: URI path
	 * </ul>
	 *
	 * @param request servlet, used to get the URI
	 * @param data    content that will be included in the response
	 * @param message response message, if applicable
	 * @param status  status code
	 * @return <code>HttpResponse</code>
	 */
	@Deprecated
	public static <T> HttpResponse<T> getResponse(
		HttpServletRequest request, T data, String message, HttpStatus status) {
		return HttpResponse.<T>builder()
			.timestamp(OffsetDateTime.now().toString())
			.data(data)
			.code(status.value())
			.message(message)
			.path(request.getRequestURI())
			.build();
	}

	@Deprecated
	public static <T> HttpResponse<T> getResponse(HttpServletRequest request, T data, HttpStatus status) {
		return getResponse(request, data, null, status);
	}

	@Deprecated
	public static HttpResponse<Void> getResponse(
		HttpServletRequest request, String message, HttpStatus status) {
		return getResponse(request, null, message, status);
	}

	@Deprecated
	public static HttpErrorResponse getErrorResponse(
		HttpServletRequest request, HttpStatus status, String message, Map<String, String> errors) {
		return new HttpErrorResponse(
			OffsetDateTime.now().toString(), status.value(), message, errors, request.getRequestURI());
	}

	@Deprecated
	public static HttpErrorResponse getErrorResponse(
		HttpServletRequest request, HttpStatus status, String message) {
		return getErrorResponse(request, status, message, null);
	}

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
