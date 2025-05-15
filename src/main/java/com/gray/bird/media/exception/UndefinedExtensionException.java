package com.gray.bird.media.exception;

public class UndefinedExtensionException extends MediaException {
	public UndefinedExtensionException(String filename) {
		super("The file has no extension: " + filename);
	}
}
