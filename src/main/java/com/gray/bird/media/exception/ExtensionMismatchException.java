package com.gray.bird.media.exception;

public class ExtensionMismatchException extends MediaException {
	public ExtensionMismatchException(String filename, String contentType) {
		super("The file extension does not match the content type: " + filename + ", " + contentType);
	}
}
