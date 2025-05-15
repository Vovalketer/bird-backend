package com.gray.bird.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;

import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.gray.bird.media.dto.ExtractedMetadata;
import com.gray.bird.media.dto.MediaDto;
import com.gray.bird.media.dto.request.MediaContentRequest;
import com.gray.bird.media.dto.request.MediaRequest;
import com.gray.bird.media.exception.DuplicateFilenameException;
import com.gray.bird.media.exception.MediaDatabaseException;
import com.gray.bird.media.exception.MediaProcessingException;
import com.gray.bird.media.utils.FilenameGenerator;
import com.gray.bird.media.utils.MediaMetadataUtils;
import com.gray.bird.storage.StorageService;
import com.gray.bird.storage.dto.StorageResult;
import com.gray.bird.utils.TestMediaFactory;

@ExtendWith(SpringExtension.class)
public class MediaServiceTest {
	@Mock
	private StorageService storageService;
	@Mock
	private MediaRepository mediaRepository;
	@Mock
	private MediaMapper mediaMapper;
	@Mock
	private MediaMetadataUtils metadataUtils;
	@Mock
	private FilenameGenerator filenameGenerator;
	@Mock
	private MediaRequestValidator validator;
	@InjectMocks
	private MediaService mediaService;

	private static final long postId = 1L;
	private static final UUID userId = UUID.randomUUID();

	@Test
	void shouldFailFastWhenValidationFails() {
		var file1 = Mockito.mock(MultipartFile.class);
		Mockito.when(file1.getOriginalFilename()).thenReturn("dup.png");
		var file2 = Mockito.mock(MultipartFile.class);
		Mockito.when(file2.getOriginalFilename()).thenReturn("dup.png");

		MediaRequest req = new MediaRequest(List.of(new MediaContentRequest(0, file1, Optional.empty()),
			new MediaContentRequest(1, file2, Optional.empty())));

		Mockito.doThrow(new DuplicateFilenameException("dup.png"))
			.when(validator)
			.validateRequestConstraints(req);

		Assertions.assertThatThrownBy(() -> mediaService.uploadImages(userId, 1L, req))
			.isInstanceOf(DuplicateFilenameException.class);

		Mockito.verify(validator).validateRequestConstraints(req);
		Mockito.verifyNoMoreInteractions(validator, metadataUtils, storageService, mediaRepository);
	}

	@Test
	void shouldReturnEmptyListWhenRequestIsEmpty() {
		List<MediaDto> result1 = mediaService.uploadImages(userId, postId, null);
		List<MediaDto> result2 =
			mediaService.uploadImages(userId, postId, new MediaRequest(Collections.emptyList()));

		assertThat(result1).isEmpty();
		assertThat(result2).isEmpty();
		verifyNoInteractions(validator, storageService, mediaRepository, mediaMapper);
	}

	@Test
	void shouldThrowWhenInputStreamFailsDuringMetadataExtraction() throws IOException {
		MultipartFile file = Mockito.mock(MultipartFile.class);
		Mockito.when(file.getOriginalFilename()).thenReturn("testFile.png");
		Mockito.when(file.getInputStream()).thenThrow(new IOException("Simulated stream error"));
		MediaContentRequest mediaContentRequest = new MediaContentRequest(0, file);
		MediaRequest request = new MediaRequest(List.of(mediaContentRequest));

		Mockito.doNothing().when(validator).validateRequestConstraints(Mockito.any(MediaRequest.class));

		Assertions.assertThatThrownBy(() -> mediaService.uploadImages(userId, postId, request))
			.isInstanceOf(MediaProcessingException.class);
	}

	@Test
	void shouldThrowWhenInputStreamFailsDuringStorageRequestMapping() throws IOException {
		MultipartFile file = Mockito.mock(MultipartFile.class);
		Mockito.when(file.getOriginalFilename()).thenReturn("testFile.png");
		Mockito.when(file.getInputStream()).thenThrow(new IOException("Simulated stream error"));
		MediaContentRequest mediaContentRequest = new MediaContentRequest(0, file);
		MediaRequest request = new MediaRequest(List.of(mediaContentRequest));

		Mockito.doNothing().when(validator).validateRequestConstraints(Mockito.any(MediaRequest.class));
		Mockito.doNothing().when(validator).validateMediaMetadataConstraints(Mockito.anyList());

		Assertions.assertThatThrownBy(() -> mediaService.uploadImages(userId, postId, request))
			.isInstanceOf(MediaProcessingException.class);
	}

	@Test
	void shouldDeleteFilesOnDiskWhenDatabaseFailsToSave() {
		MediaRequest request = TestMediaFactory.mediaRequestWithImages("file1.jpg");
		Mockito.doNothing().when(validator).validateRequestConstraints(Mockito.any(MediaRequest.class));
		Mockito.doNothing().when(validator).validateMediaMetadataConstraints(Mockito.anyList());

		ExtractedMetadata metadata = new ExtractedMetadata("file1.jpg", ".jpg", "image/jpeg", 100, 100, 0);
		Mockito.when(metadataUtils.extractMetadata(Mockito.any(InputStream.class), Mockito.anyString()))
			.thenReturn(metadata);

		List<StorageResult> storageResult =
			List.of(new StorageResult("file1.jpg", "file1.jpg", ".jpg", 1000, "./file1.jpg", null));
		Mockito.when(storageService.saveAll(Mockito.anyList())).thenReturn(storageResult);

		Mockito.doThrow(new DataIntegrityViolationException("Simulated database error"))
			.when(mediaRepository)
			.saveAll(Mockito.anyList());

		Assertions.assertThatThrownBy(() -> mediaService.uploadImages(userId, postId, request))
			.isInstanceOf(MediaDatabaseException.class);

		Mockito.verify(storageService).deleteAll(List.of("./file1.jpg"));
	}

	@Test
	void shouldCreateMediaWhenMediaRequestContainsOneImage() throws IOException {
		String extension1 = ".jpg";
		String filename1 = "testFile1" + extension1;
		String storageFilename1 = userId.toString() + extension1;
		String mimeType1 = "image/jpeg";

		MediaRequest mediaRequest = TestMediaFactory.mediaRequestWithImages(filename1);
		Mockito.doNothing().when(validator).validateRequestConstraints(Mockito.any(MediaRequest.class));
		Mockito.doNothing().when(validator).validateMediaMetadataConstraints(Mockito.anyList());
		ExtractedMetadata metadata = new ExtractedMetadata(filename1, extension1, mimeType1, 100, 100, 1);
		Mockito.when(metadataUtils.extractMetadata(Mockito.any(InputStream.class), Mockito.anyString()))
			.thenReturn(metadata);

		Mockito.when(filenameGenerator.generateFilename(Mockito.anyString())).thenReturn(storageFilename1);

		Resource fileResource = Mockito.mock(Resource.class);
		List<StorageResult> storageResult = List.of(new StorageResult(
			storageFilename1, filename1, extension1, 1000, "./" + storageFilename1, fileResource));
		Mockito.when(storageService.saveAll(Mockito.anyList())).thenReturn(storageResult);

		List<MediaEntity> mediaEntities =
			TestMediaFactory.mediaEntities(postId, userId, mimeType1, List.of(filename1));
		Mockito.when(mediaRepository.saveAll(Mockito.anyList())).thenReturn(mediaEntities);

		List<MediaDto> mediaDtos = TestMediaFactory.mediaDtos(postId, userId, mimeType1, List.of(filename1));
		Mockito.when(mediaMapper.toMediaDto(Mockito.any(MediaEntity.class))).thenReturn(mediaDtos.getFirst());

		List<MediaDto> result = mediaService.uploadImages(userId, postId, mediaRequest);
		assertThat(result).hasSize(1);
	}

	// @Test
	// void shouldCreateMediaWhenMediaRequestContainsMultipleImages() throws IOException {
	// 	String extension1 = ".jpg";
	// 	String filename1 = "testFile1" + extension1;
	// 	String storageFilename1 = userId.toString() + extension1;
	// 	String mimeType1 = "image/jpeg";
	// 	String extension2 = ".png";
	// 	String filename2 = "testFile2" + extension2;
	// 	String storageFilename2 = userId.toString() + extension2;
	// 	String mimeType2 = "image/png";
	// 	String extension3 = ".jpg";
	// 	String filename3 = "testFile3" + extension3;
	// 	String storageFilename3 = userId.toString() + extension3;
	// 	String mimeType3 = "image/jpeg";
	// 	String extension4 = ".png";
	// 	String filename4 = "testFile4" + extension4;
	// 	String storageFilename4 = userId.toString() + extension4;
	// 	String mimeType4 = "image/png";
	//
	// 	MediaRequest mediaRequest =
	// 		TestMediaFactory.mediaRequestWithImages(filename1, filename2, filename3, filename4);
	// 	Mockito.doNothing().when(validator).validateRequestConstraints(Mockito.any(MediaRequest.class));
	// 	Mockito.doNothing().when(validator).validateMetadata(Mockito.anyList());
	// 	ExtractedMetadata metadata1 = new ExtractedMetadata(filename1, extension1, mimeType1, 100, 100, 0);
	// 	ExtractedMetadata metadata2 = new ExtractedMetadata(filename2, extension2, mimeType2, 100, 100, 0);
	// 	ExtractedMetadata metadata3 = new ExtractedMetadata(filename3, extension3, mimeType3, 100, 100, 0);
	// 	ExtractedMetadata metadata4 = new ExtractedMetadata(filename4, extension4, mimeType4, 100, 100, 0);
	// }
}
