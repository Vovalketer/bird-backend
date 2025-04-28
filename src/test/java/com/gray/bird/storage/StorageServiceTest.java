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
import java.util.Map;

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
		void shouldThrowWhenAttemptingToSaveAnEmptyFile() {
			MockMultipartFile file = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);
			Assertions.assertThatThrownBy(() -> { storageService.save("EmptyFile.png", file); })
				.isInstanceOf(EmptyFileException.class);
		}

		@Test
		void shouldThrowWhenAttemptingToSaveFileWithEmptyFilename() {
			MockMultipartFile file =
				new MockMultipartFile("file", "empty.png", "image/png", "testcontent".getBytes());
			Assertions.assertThatThrownBy(() -> { storageService.save("", file); })
				.isInstanceOf(EmptyFilenameException.class);
		}

		@Test
		void shouldThrowWhenAttemptingToSaveFileWithNullFilename() {
			MockMultipartFile file =
				new MockMultipartFile("file", "empty.png", "image/png", "testcontent".getBytes());
			Assertions.assertThatThrownBy(() -> { storageService.save(null, file); })
				.isInstanceOf(EmptyFilenameException.class);
		}

		@Test
		void shouldThrowWhenFileFailsToBeStored() {
			try (var files = Mockito.mockStatic(Files.class)) {
				files.when(() -> Files.copy(Mockito.any(InputStream.class), Mockito.any(Path.class)))
					.thenThrow(IOException.class);

				MockMultipartFile file =
					new MockMultipartFile("file", "someFile.png", "image/png", "testcontent".getBytes());

				Assertions.assertThatThrownBy(() -> { storageService.save("SomeFile.png", file); })
					.isInstanceOf(FileSaveException.class);
			}
		}

		@Test
		void shouldThrowWhenBulkSaveFailsToStoreFiles() {
			try (var files = Mockito.mockStatic(Files.class)) {
				files.when(() -> Files.copy(Mockito.any(InputStream.class), Mockito.any(Path.class)))
					.thenThrow(IOException.class);

				MockMultipartFile file1 =
					new MockMultipartFile("file", "someFile.png", "image/png", "testcontent".getBytes());
				MockMultipartFile file2 =
					new MockMultipartFile("file", "someFile2.png", "image/png", "testcontent".getBytes());
				MockMultipartFile file3 =
					new MockMultipartFile("file", "someFile3.png", "image/png", "testcontent".getBytes());

				Assertions
					.assertThatThrownBy(() -> {
						storageService.saveAll(
							Map.of("someFile.png", file1, "someFile2.png", file2, "someFile3.png", file3));
					})
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
				files.when(() -> Files.exists(Mockito.any(Path.class))).thenReturn(false);

				Assertions
					.assertThatThrownBy(() -> { storageService.getFileAsResource("nonExistentFile.png"); })
					.isInstanceOf(FileNotFoundException.class);
			}
		}
	}
}
