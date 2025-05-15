package com.gray.bird.media.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

import com.gray.bird.media.dto.ExtractedMetadata;
import com.gray.bird.utils.TestMediaFactory;

@SpringBootTest(classes = MediaMetadataUtils.class)
public class MetadataUtilsIT {
	@Autowired
	private MediaMetadataUtils metadataUtils;

	@ParameterizedTest
	@CsvSource({"image/jpeg,.jpg", "image/png,.png", "image/gif,.gif"})
	void shouldExtractMetadataFromImages(String contentType, String extension) throws IOException {
		int height = 100;
		int width = 100;
		String filename = "test" + extension;
		MultipartFile image =
			TestMediaFactory.image(filename, extension, contentType, width, height, Color.RED);

		try (InputStream inputStream = image.getInputStream()) {
			ExtractedMetadata metadata = metadataUtils.extractMetadata(inputStream, filename);

			Assertions.assertThat(metadata.width()).isEqualTo(width);
			Assertions.assertThat(metadata.height()).isEqualTo(height);
			Assertions.assertThat(metadata.extension()).isEqualTo(extension);
			Assertions.assertThat(metadata.mimeType()).isEqualTo(contentType);
			Assertions.assertThat(metadata.filename()).isEqualTo(filename);
			Assertions.assertThat(metadata.orientation()).isEqualTo(0);
		}
	}

	@ParameterizedTest
	@CsvSource({"image/jpeg,.jpg", "image/png,.png", "image/gif,.gif"})
	void shouldValidateImageContentType(String contentType, String extension) {
		boolean isValid = metadataUtils.isContentTypeValid(contentType, extension);
		Assertions.assertThat(isValid).isTrue();
	}

	@ParameterizedTest
	@CsvSource({"image/jpeg,.png", "image/png,.jpg", "image/gif,.jpg"})
	void shouldNotValidateWhenContentAndExtensionDontMatch(String contentType, String extension) {
		boolean isValid = metadataUtils.isContentTypeValid(contentType, extension);
		Assertions.assertThat(isValid).isFalse();
	}
}
