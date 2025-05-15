package com.gray.bird.media.exception;

import com.gray.bird.media.MediaConstants;

public class FilenameLengthExceededException extends MediaException {
	public FilenameLengthExceededException(int filenameLength) {
		super("Filename length exceeded. The allowed length is " + MediaConstants.MEDIA_FILENAME_LENGTH_LIMIT
			+ "but the filename length was " + filenameLength);
	}
}
