package com.gray.bird.media.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

import com.gray.bird.media.exception.UndefinedExtensionException;

@Component
public class FilenameGenerator {
	public String generateFilename(String originalFilename) {
		String extension = getExtension(originalFilename);
		return UUID.randomUUID().toString().concat(extension);
	}

	private String getExtension(String filename) {
		int index = filename.lastIndexOf('.');
		if (index == -1) {
			throw new UndefinedExtensionException(filename);
		}
		return filename.substring(index);
	}
}
