package com.gray.bird.media;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import com.gray.bird.media.dto.ExtractedMetadata;
import com.gray.bird.media.dto.MediaMetadata;
import com.gray.bird.media.dto.request.MediaRequest;
import com.gray.bird.media.exception.DuplicateFilenameException;
import com.gray.bird.media.exception.FilenameLengthExceededException;
import com.gray.bird.media.exception.MediaLimitExceededException;
import com.gray.bird.media.exception.UnsupportedMediaTypeException;
import com.gray.bird.utils.TestMediaFactory;

@ExtendWith(SpringExtension.class)
public class MediaRequestValidatorTest {
	private MediaRequestValidator validator = new MediaRequestValidator();

	@Nested
	class RequestTest {
		@Test
		void shouldThrowWhenContainsTooManyFiles() {
			MediaRequest request = TestMediaFactory.mediaRequestWithImages(
				"testFile1.jpg", "testFile2.jpg", "testFile3.jpg", "testFile4.jpg", "testFile5.jpg");

			Assertions.assertThatThrownBy(() -> validator.validateRequestConstraints(request))
				.isInstanceOf(MediaLimitExceededException.class);
		}

		@Test
		void shouldThrowWhenContainsDuplicateFilenames() {
			MediaRequest request =
				TestMediaFactory.mediaRequestWithImages("testFile1.jpg", "testFile2.jpg", "testFile1.jpg");

			Assertions.assertThatThrownBy(() -> validator.validateRequestConstraints(request))
				.isInstanceOf(DuplicateFilenameException.class);
		}
	}

	@Nested
	class ExtractedMetadataTest {
		@Test
		void shouldThrowWhenExtractedMetadataContainsUnsupportedMimeType() {
			ExtractedMetadata metadata = TestMediaFactory.extractedMetadata("testFile1.jpg", "image/unknown");

			Assertions.assertThatThrownBy(() -> validator.validateExtractedMetadataConstraints(metadata))
				.isInstanceOf(UnsupportedMediaTypeException.class);
		}

		@Test
		void shouldThrowWhenExtractedMetadataFilenameExceedsLimit() {
			ExtractedMetadata metadata = TestMediaFactory.extractedMetadata(
				"a".repeat(MediaConstants.MEDIA_FILENAME_LENGTH_LIMIT + 1) + ".jpg", "image/jpeg");

			Assertions.assertThatThrownBy(() -> validator.validateExtractedMetadataConstraints(metadata))
				.isInstanceOf(FilenameLengthExceededException.class);
		}
	}

	@Nested
	class MediaMetadataTest {
		@Test
		void shouldThrowWhenMediaMetadataContainsUnsupportedMimeType() {
			MediaMetadata mediaMetadata = TestMediaFactory.mediaMetadata("testFile1.jpg", "image/unknown");

			Assertions
				.assertThatThrownBy(() -> validator.validateMediaMetadataConstraints(List.of(mediaMetadata)))
				.isInstanceOf(UnsupportedMediaTypeException.class);
		}

		@Test
		void shouldThrowWhenMediaMetadataFilenameExceedsLimit() {
			MediaMetadata metadata = TestMediaFactory.mediaMetadata(
				"a".repeat(MediaConstants.MEDIA_FILENAME_LENGTH_LIMIT + 1) + ".jpg", "image/jpeg");

			Assertions.assertThatThrownBy(() -> validator.validateMediaMetadataConstraints(List.of(metadata)))
				.isInstanceOf(FilenameLengthExceededException.class);
		}
	}
}
