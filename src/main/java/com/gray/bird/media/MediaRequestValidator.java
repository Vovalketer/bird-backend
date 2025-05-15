package com.gray.bird.media;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gray.bird.media.dto.ExtractedMetadata;
import com.gray.bird.media.dto.MediaMetadata;
import com.gray.bird.media.dto.request.MediaRequest;
import com.gray.bird.media.exception.DuplicateFilenameException;
import com.gray.bird.media.exception.FilenameLengthExceededException;
import com.gray.bird.media.exception.MediaLimitExceededException;
import com.gray.bird.media.exception.UnsupportedMediaTypeException;

@Service
public class MediaRequestValidator {
	public void validateRequestConstraints(MediaRequest request) {
		if (request.content().size() > MediaConstants.MEDIA_FILE_LIMIT) {
			throw new MediaLimitExceededException(MediaConstants.MEDIA_FILE_LIMIT);
		}
		Map<String, Long> counts = request.content().stream().collect(
			Collectors.groupingBy(c -> c.file().getOriginalFilename(), Collectors.counting()));
		List<String> duplicateFilenames =
			counts.entrySet().stream().filter(e -> e.getValue() > 1).map(Map.Entry::getKey).toList();
		if (!duplicateFilenames.isEmpty()) {
			throw new DuplicateFilenameException(String.join(", ", duplicateFilenames));
		}
	}

	public void validateExtractedMetadataConstraints(ExtractedMetadata metadata) {
		boolean isMimeTypeAllowed = MediaConstants.ALLOWED_IMAGE_TYPES.contains(metadata.mimeType());
		if (!isMimeTypeAllowed) {
			throw new UnsupportedMediaTypeException(metadata.mimeType());
		}
		int filenameLength = metadata.filename().length();
		if (filenameLength > MediaConstants.MEDIA_FILENAME_LENGTH_LIMIT) {
			throw new FilenameLengthExceededException(filenameLength);
		}
		// NOTE: the file size limit of each file should be validated by spring itself through
		// the property spring.servlet.multipart.max-file-size
	}

	public void validateMediaMetadataConstraints(Collection<MediaMetadata> metadata) {
		metadata.forEach(m -> { validateExtractedMetadataConstraints(m.extractedMetadata()); });
	}
}
