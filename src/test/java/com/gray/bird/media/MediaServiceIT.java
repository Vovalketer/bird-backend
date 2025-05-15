package com.gray.bird.media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import com.gray.bird.media.dto.MediaDto;
import com.gray.bird.media.dto.request.MediaContentRequest;
import com.gray.bird.media.dto.request.MediaRequest;
import com.gray.bird.media.exception.ExtensionMismatchException;
import com.gray.bird.media.exception.MediaDatabaseException;
import com.gray.bird.testConfig.TestcontainersConfig;
import com.gray.bird.utils.TestMediaFactory;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestcontainersConfig.class)
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MediaServiceIT {
	private static final long postId = 1L;
	private static final UUID userId = UUID.randomUUID();
	@Autowired
	private MediaService mediaService;
	@MockitoSpyBean
	private MediaRepository mediaRepository;

	// NOTE: set the temp dir manually because the annotation doesnt work well with DI(its null when it
	// happens)
	// use ./target because it gets cleaned up during the normal lifecycle, just in case
	static Path tempDir = Path.of("target/test-classes/temp");

	@DynamicPropertySource
	static void overrideStoragePath(DynamicPropertyRegistry registry) {
		registry.add("storage.media-path", () -> tempDir.toString());
	}

	@AfterEach
	void tearDown() throws IOException {
		mediaRepository.deleteAll();
		Mockito.reset(mediaRepository);
		Files.walk(tempDir).filter(p -> !p.equals(tempDir)).forEach(p -> p.toFile().delete());
	}

	@Test
	void shouldSaveOneImage() {
		MediaRequest request = TestMediaFactory.mediaRequestWithImages("testFile1.jpg");

		List<MediaDto> result = mediaService.uploadImages(userId, postId, request);

		Assertions.assertThat(result).hasSize(1);
		Assertions.assertThat(result.get(0).originalFilename()).isEqualTo("testFile1.jpg");

		// check the contents of the database
		Assertions.assertThat(mediaRepository.count()).isEqualTo(1);
		MediaEntity mediaEntity = mediaRepository.findById(result.get(0).id()).get();
		// dont assert the filename, as its generated internally
		Assertions.assertThat(mediaEntity.getMimeType()).isEqualTo("image/jpeg");
		Assertions.assertThat(mediaEntity.getOriginalFilename()).isEqualTo("testFile1.jpg");
		Assertions.assertThat(mediaEntity.getPostId()).isEqualTo(postId);
		Assertions.assertThat(mediaEntity.getUserId()).isEqualTo(userId);
		Assertions.assertThat(mediaEntity.getSortOrder()).isEqualTo(0);
		Assertions.assertThat(mediaEntity.getWidth()).isEqualTo(10);
		Assertions.assertThat(mediaEntity.getHeight()).isEqualTo(10);
		Assertions.assertThat(mediaEntity.getAlt()).isEqualTo("alt0");
	}

	@Test
	void shouldSaveMultipleImages() {
		MediaRequest request =
			TestMediaFactory.mediaRequestWithImages("testFile1.jpg", "testFile2.jpg", "testFile3.jpg");

		List<MediaDto> result = mediaService.uploadImages(userId, postId, request);

		Assertions.assertThat(result).hasSize(3);

		// check the contents of the database
		Assertions.assertThat(mediaRepository.count()).isEqualTo(3);
		List<String> originalFilenames =
			mediaRepository.findAll().stream().map(m -> m.getOriginalFilename()).toList();
		Assertions.assertThat(originalFilenames)
			.containsExactlyInAnyOrder("testFile1.jpg", "testFile2.jpg", "testFile3.jpg");

		// check the existence of the files
		List<String> relativePaths = result.stream().map(r -> r.relativePath()).toList();
		relativePaths.stream()
			.map(p -> tempDir.resolve(p))
			.forEach(p -> Assertions.assertThat(Files.exists(p)));
	}

	@Test
	void emptyRequestShouldReturnEmptyListWithoutSideEffects() throws IOException {
		MediaRequest emptyMediaRequest = TestMediaFactory.emptyMediaRequest();

		List<MediaDto> result = mediaService.uploadImages(userId, postId, emptyMediaRequest);

		Assertions.assertThat(result).isEmpty();
		Assertions.assertThat(mediaRepository.count()).isEqualTo(0);
		Assertions.assertThat(Files.list(tempDir)).isEmpty();
	}

	@Test
	void shouldThrowWhenInvalidImageIsSent() {
		MockMultipartFile badFile =
			new MockMultipartFile("badImage", "badImage.jpg", "image/jpeg", "invalidContent".getBytes());
		MediaRequest request = new MediaRequest(List.of(new MediaContentRequest(0, badFile)));

		Assertions.assertThatThrownBy(() -> mediaService.uploadImages(userId, postId, request))
			.isInstanceOf(ExtensionMismatchException.class);
	}

	@Test
	void shouldRollbackSavedFilesWhenDatabaseFails() throws IOException {
		MediaRequest request =
			TestMediaFactory.mediaRequestWithImages("testFile1.jpg", "testFile2.jpg", "testFile3.jpg");
		Mockito.doThrow(DataIntegrityViolationException.class).when(mediaRepository).saveAll(Mockito.any());

		Assertions.assertThatThrownBy(() -> mediaService.uploadImages(userId, postId, request))
			.isInstanceOf(MediaDatabaseException.class);

		Assertions.assertThat(mediaRepository.count()).isEqualTo(0);
		Assertions.assertThat(Files.list(tempDir)).isEmpty();
	}

	@Test
	void shouldMaintainInsertionOrder() {
		MediaRequest request =
			TestMediaFactory.mediaRequestWithImages("testFile1.jpg", "testFile2.jpg", "testFile3.jpg");

		List<MediaDto> result = mediaService.uploadImages(userId, postId, request);

		Assertions.assertThat(result).hasSize(3);

		Assertions.assertThat(result.get(0).originalFilename()).isEqualTo("testFile1.jpg");
		Assertions.assertThat(result.get(1).originalFilename()).isEqualTo("testFile2.jpg");
		Assertions.assertThat(result.get(2).originalFilename()).isEqualTo("testFile3.jpg");
	}
}
