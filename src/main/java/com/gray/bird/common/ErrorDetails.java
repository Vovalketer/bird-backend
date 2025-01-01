package com.gray.bird.common;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Deprecated
public class ErrorDetails {
	private Map<String, String> errors;

	public ErrorDetails() {
		errors = new HashMap<>();
	}

	public ErrorDetails(Map<String, String> errors) {
		this.errors = errors;
	}

	public void add(String name, String errorMsg) {
		errors.put(name, errorMsg);
	}

	public void remove(String name) {
		errors.remove(name);
	}

	public boolean containsError(String name) {
		return errors.containsKey(name);
	}

	public int size() {
		return errors.size();
	}

	public void forEach(BiConsumer<? super String, ? super String> action) {
		errors.forEach(action);
	}

	public String get(String name) {
		return errors.get(name);
	}

	public boolean isEmpty() {
		return errors.isEmpty();
	}

	public void putIfAbsent(String name, String errorMsg) {
		errors.putIfAbsent(name, errorMsg);
	}

	@Override
	public String toString() {
		return errors.toString();
	}
}
