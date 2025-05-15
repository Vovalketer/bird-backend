package com.gray.bird.media;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.gray.bird.media.dto.response.MediaFile;

@ExtendWith(SpringExtension.class)
public class MediaControllerTest {
	@Mock
	private MediaService mediaService;
	@InjectMocks
	private MediaController controller;

	@Test
	void testGetMedia() {
		MockMultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", new byte[100]);
		MediaFile media = new MediaFile(file.getResource(), "test.jpg", "image/jpeg", 100);

		Mockito.when(mediaService.getMedia("test.jpg")).thenReturn(media);

		ResponseEntity<Resource> result = controller.getMedia("test.jpg");

		Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(result.getBody()).isEqualTo(media.resource());
		Assertions.assertThat(result.getHeaders().getContentType().toString()).isEqualTo(media.contentType());
		Assertions.assertThat(result.getHeaders().getContentLength()).isEqualTo(media.fileSize());
		Assertions.assertThat(result.getHeaders().getContentDisposition().getFilename())
			.isEqualTo(media.originalFilename());
	}
}
