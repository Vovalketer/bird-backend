package com.gray.bird.storage.utils;

import java.nio.file.Path;

import com.gray.bird.storage.exception.InvalidPathException;

public class PathUtils {
	public Path build() {
		return null;
	}

	public Path resolve(Path basePath, String relativePath) {
		Path path = null;
		try {
			path = basePath.resolve(relativePath).normalize();

		} catch (java.nio.file.InvalidPathException e) {
			throw new InvalidPathException(relativePath);
		}
		validatePath(basePath, path);
		return path;
	}

	private void validatePath(Path basePath, Path path) {
		if (!path.startsWith(basePath)) {
			throw new InvalidPathException(path.toString());
		}
	}
}
