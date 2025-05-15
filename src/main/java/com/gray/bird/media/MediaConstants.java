package com.gray.bird.media;

import java.util.Set;

public class MediaConstants {
	public static final Set<String> ALLOWED_IMAGE_TYPES =
		Set.of("image/png", "image/jpeg", "image/gif", "image/webp");
	public static final Set<String> ALLOWED_VIDEO_TYPES = Set.of("video/mp4", "video/H264", "video/mpeg");
	public static final int MEDIA_FILE_LIMIT = 4;
	public static final int MEDIA_FILENAME_LENGTH_LIMIT = 255;
}
