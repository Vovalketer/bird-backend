package com.gray.bird.storage;

import org.springframework.mock.web.MockMultipartFile;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.gray.bird.storage.dto.StorageRequest;
import com.gray.bird.storage.exception.BulkSaveOperationException;
import com.gray.bird.storage.exception.DirectoryCreationException;
import com.gray.bird.storage.exception.EmptyFileException;
import com.gray.bird.storage.exception.EmptyFilenameException;
import com.gray.bird.storage.exception.FileDeleteException;
import com.gray.bird.storage.exception.FileNotFoundException;
import com.gray.bird.storage.exception.FileSaveException;
import com.gray.bird.storage.exception.InvalidPathException;

public class StorageServiceTest {
	private StorageService storageService;
	// probably should mock this as well
	@TempDir
	private Path tempDir;

	@BeforeEach
	void setUp() {
		storageService = new StorageService(tempDir);
	}

	@Nested
	class Exceptions {
		@Test
		void shouldThrowWhenBaseDirectoryCantBeCreated() {
			try (MockedStatic<Files> files = Mockito.mockStatic(Files.class)) {
				files.when(() -> Files.createDirectories(Mockito.any(Path.class)))
					.thenThrow(IOException.class);
				Assertions.assertThatThrownBy(() -> new StorageService(Paths.get("test")))
					.isInstanceOf(DirectoryCreationException.class);
			}
		}

		@Test
		void shouldThrowWhenAttemptingToSaveAnEmptyFile() throws IOException {
			MockMultipartFile file = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);
			StorageRequest request = StorageRequest.builder()
										 .fileStream(file.getInputStream())
										 .originalFilename(file.getOriginalFilename())
										 .targetFilename(file.getName())
										 .fileSize(file.getSize())
										 .build();
			Assertions.assertThatThrownBy(() -> { storageService.save(request); })
				.isInstanceOf(EmptyFileException.class);
		}

		@Test
		void shouldThrowWhenAttemptingToSaveFileWithEmptyFilename() throws IOException {
			MockMultipartFile file =
				new MockMultipartFile("file", "empty.png", "image/png", "testcontent".getBytes());
			StorageRequest request = StorageRequest.builder()
										 .fileStream(file.getInputStream())
										 .originalFilename(file.getOriginalFilename())
										 .targetFilename("")
										 .fileSize(file.getSize())
										 .build();
			Assertions.assertThatThrownBy(() -> { storageService.save(request); })
				.isInstanceOf(EmptyFilenameException.class);
		}

		@Test
		void shouldThrowWhenAttemptingToSaveFileWithNullFilename() throws IOException {
			MockMultipartFile file =
				new MockMultipartFile("file", "empty.png", "image/png", "testcontent".getBytes());
			StorageRequest request = StorageRequest.builder()
										 .fileStream(file.getInputStream())
										 .originalFilename(file.getOriginalFilename())
										 .targetFilename(null)
										 .fileSize(file.getSize())
										 .build();
			Assertions.assertThatThrownBy(() -> { storageService.save(request); })
				.isInstanceOf(EmptyFilenameException.class);
		}

		@Test
		void shouldThrowWhenFileFailsToBeStored() throws IOException {
			try (var files = Mockito.mockStatic(Files.class)) {
				files.when(() -> Files.copy(Mockito.any(InputStream.class), Mockito.any(Path.class)))
					.thenThrow(IOException.class);

				MockMultipartFile file =
					new MockMultipartFile("file", "someFile.png", "image/png", "testcontent".getBytes());
				StorageRequest request = StorageRequest.builder()
											 .fileStream(file.getInputStream())
											 .originalFilename(file.getOriginalFilename())
											 .targetFilename(file.getName())
											 .fileSize(file.getSize())
											 .build();

				Assertions.assertThatThrownBy(() -> { storageService.save(request); })
					.isInstanceOf(FileSaveException.class);
			}
		}

		@Test
		void shouldThrowWhenBulkSaveFailsToStoreFiles() throws IOException {
			try (var files = Mockito.mockStatic(Files.class)) {
				files.when(() -> Files.copy(Mockito.any(InputStream.class), Mockito.any(Path.class)))
					.thenThrow(IOException.class);

				MockMultipartFile file1 =
					new MockMultipartFile("file", "someFile.png", "image/png", "testcontent".getBytes());
				MockMultipartFile file2 =
					new MockMultipartFile("file", "someFile2.png", "image/png", "testcontent".getBytes());
				MockMultipartFile file3 =
					new MockMultipartFile("file", "someFile3.png", "image/png", "testcontent".getBytes());
				List<StorageRequest> requests = List.of(StorageRequest.builder()
															.fileStream(file1.getInputStream())
															.originalFilename(file1.getOriginalFilename())
															.targetFilename(file1.getName())
															.fileSize(file1.getSize())
															.build(),
					StorageRequest.builder()
						.fileStream(file2.getInputStream())
						.originalFilename(file2.getOriginalFilename())
						.targetFilename(file2.getName())
						.fileSize(file2.getSize())
						.build(),
					StorageRequest.builder()
						.fileStream(file3.getInputStream())
						.originalFilename(file3.getOriginalFilename())
						.targetFilename(file3.getName())
						.fileSize(file3.getSize())
						.build());

				Assertions.assertThatThrownBy(() -> { storageService.saveAll(requests); })
					.isInstanceOf(BulkSaveOperationException.class);
			}
		}

		@Test
		void shouldThrowWhenFileFailsToBeDeleted() {
			try (var files = Mockito.mockStatic(Files.class)) {
				files.when(() -> Files.deleteIfExists(Mockito.any(Path.class))).thenThrow(IOException.class);

				Assertions.assertThatThrownBy(() -> { storageService.delete("someFile.png"); })
					.isInstanceOf(FileDeleteException.class);
			}
		}

		@Test
		void shouldThrowWhenAttemptingToGetPathOutsideTheCurrentDirectory() {
			Assertions.assertThatThrownBy(() -> { storageService.getPath("/outside/tempDir"); })
				.isInstanceOf(InvalidPathException.class);
		}

		@Test
		void shouldThrowWhenPathFailsToBeResolved() {
			Assertions.assertThatThrownBy(() -> { storageService.getPath("bad\0name"); })
				.isInstanceOf(InvalidPathException.class);
		}

		@Test
		void shouldThrowWhenAttemptingToGetFileThatDoesNotExist() {
			try (var files = Mockito.mockStatic(Files.class)) {
				files.when(() -> Files.isRegularFile(Mockito.any(Path.class))).thenReturn(false);

				Assertions
					.assertThatThrownBy(() -> { storageService.getFileAsResource("nonExistentFile.png"); })
					.isInstanceOf(FileNotFoundException.class);
			}
		}
	}
}
