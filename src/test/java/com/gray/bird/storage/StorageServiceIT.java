package com.gray.bird.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;

import com.gray.bird.storage.dto.StorageRequest;
import com.gray.bird.storage.dto.StorageResult;
import com.gray.bird.storage.exception.BulkSaveOperationException;
import com.gray.bird.storage.exception.FileNotFoundException;
import com.gray.bird.storage.exception.FileSaveException;
import com.gray.bird.storage.exception.FilenameAlreadyExistsException;
import com.gray.bird.utils.TestStorageFactory;

public class StorageServiceIT {
	@TempDir(cleanup = CleanupMode.ALWAYS)
	private Path tempDir;

	private StorageService storageService;

	@BeforeEach
	void setUp() {
		storageService = new StorageService(tempDir);
	}

	class FailingInputStream extends InputStream {
		private int failAfterBytes;
		private int readCount = 0;

		public FailingInputStream(int failAfterBytes) {
			this.failAfterBytes = failAfterBytes;
		}

		@Override
		public int read() throws IOException {
			if (readCount++ >= failAfterBytes) {
				throw new IOException("Simulated disk error");
			}
			return 'x';
		}
	}

	@Nested
	class SaveFile {
		@Test
		void shouldSaveFileWhenFileIsProvidedAndReadIt() throws Exception {
			MultipartFile file = TestStorageFactory.imageFile("red", "png", Color.RED);
			StorageRequest request = StorageRequest.builder()
										 .fileStream(file.getInputStream())
										 .originalFilename(file.getOriginalFilename())
										 .targetFilename("red.png")
										 .fileSize(file.getSize())
										 .build();

			StorageResult saved = storageService.save(request);

			// read file and verify dimensions & color
			File file2 = saved.fileResource().getFile();
			assertThat(file2).isNotNull();
			BufferedImage readBack = ImageIO.read(file2);
			assertThat(readBack.getWidth()).isEqualTo(50);
			assertThat(readBack.getHeight()).isEqualTo(50);
			// Check top-left pixel is red
			assertThat(new Color(readBack.getRGB(0, 0))).isEqualTo(Color.RED);
		}

		@Test
		void shouldSaveFilesWhenFileListIsProvidedAndReadThem() throws Exception {
			MultipartFile redFile = TestStorageFactory.imageFile("red", "png", Color.RED);
			MultipartFile blueFile = TestStorageFactory.imageFile("blue", "png", Color.BLUE);
			MultipartFile greenFile = TestStorageFactory.imageFile("green", "png", Color.GREEN);
			List<StorageRequest> requests = List.of(StorageRequest.builder()
														.fileStream(redFile.getInputStream())
														.originalFilename(redFile.getOriginalFilename())
														.targetFilename(redFile.getName())
														.fileSize(redFile.getSize())
														.build(),
				StorageRequest.builder()
					.fileStream(blueFile.getInputStream())
					.originalFilename(blueFile.getOriginalFilename())
					.targetFilename(blueFile.getName())
					.fileSize(blueFile.getSize())
					.build(),
				StorageRequest.builder()
					.fileStream(greenFile.getInputStream())
					.originalFilename(greenFile.getOriginalFilename())
					.targetFilename(greenFile.getName())
					.fileSize(greenFile.getSize())
					.build());

			List<StorageResult> saved = storageService.saveAll(requests);

			assertThat(saved).hasSize(3);

			// read files and verify dimensions & color, avoiding to depend on the output order

			List<BufferedImage> readBack = new ArrayList<>();
			for (StorageResult p : saved) {
				readBack.add(ImageIO.read(p.fileResource().getFile()));
			}

			Optional<BufferedImage> redOptional =
				// Check top-left pixel is red
				readBack.stream().filter(i -> new Color(i.getRGB(0, 0)).equals(Color.RED)).findFirst();
			assertThat(redOptional).isPresent();
			BufferedImage readBackRed = redOptional.get();
			assertThat(readBackRed.getWidth()).isEqualTo(50);
			assertThat(readBackRed.getHeight()).isEqualTo(50);

			Optional<BufferedImage> blueOptional =
				// Check top-left pixel is blue
				readBack.stream().filter(i -> new Color(i.getRGB(0, 0)).equals(Color.BLUE)).findFirst();
			assertThat(blueOptional).isPresent();
			BufferedImage readBackBlue = blueOptional.get();
			assertThat(readBackBlue.getWidth()).isEqualTo(50);
			assertThat(readBackBlue.getHeight()).isEqualTo(50);

			Optional<BufferedImage> greenOptional =
				// Check top-left pixel is green
				readBack.stream().filter(i -> new Color(i.getRGB(0, 0)).equals(Color.GREEN)).findFirst();
			assertThat(greenOptional).isPresent();
			BufferedImage readBackGreen = greenOptional.get();
			assertThat(readBackGreen.getWidth()).isEqualTo(50);
			assertThat(readBackGreen.getHeight()).isEqualTo(50);
		}

		@Test
		void shouldThrowWhenFileAlreadyExists() throws IOException {
			MultipartFile imageFile = TestStorageFactory.imageFile("red", "png", Color.RED);

			TestStorageFactory.writeFileToDisk(imageFile, "red.png", tempDir);

			StorageRequest request = StorageRequest.builder()
										 .fileStream(imageFile.getInputStream())
										 .originalFilename(imageFile.getOriginalFilename())
										 .targetFilename("red.png")
										 .fileSize(imageFile.getSize())
										 .build();
			assertThatThrownBy(() -> storageService.save(request))
				.isInstanceOf(FilenameAlreadyExistsException.class);
		}

		@Test
		void shouldRollBackWhenBulkSaveFailsToStoreAFile() throws IOException {
			MultipartFile file = TestStorageFactory.imageFile("red", "png", Color.RED);
			MultipartFile file2 = TestStorageFactory.imageFile("blue", "png", Color.BLUE);
			MultipartFile invalidFile = TestStorageFactory.imageFile("green", "png", Color.GREEN);
			// InputStream invalidStream = Mockito.mock(InputStream.class);
			InputStream inputStream = new FailingInputStream(10);
			StorageRequest invalidRequest = StorageRequest.builder()
												.fileStream(inputStream)
												.originalFilename(invalidFile.getOriginalFilename())
												.targetFilename(invalidFile.getName())
												.fileSize(invalidFile.getSize())
												.build();
			// Mockito.when(invalidStream.read()).thenThrow(new IOException("Simulated disk error"));

			List<StorageRequest> requests = List.of(StorageRequest.builder()
														.fileStream(file.getInputStream())
														.originalFilename(file.getOriginalFilename())
														.targetFilename(file.getName())
														.fileSize(file.getSize())
														.build(),
				StorageRequest.builder()
					.fileStream(file2.getInputStream())
					.originalFilename(file2.getOriginalFilename())
					.targetFilename(file2.getName())
					.fileSize(file2.getSize())
					.build(),
				invalidRequest);
			// spaghetti
			// choices are testing the rollback feature in a unit test, mocking the files class or making a
			// helper class for the verification methods

			assertThatThrownBy(() -> { storageService.saveAll(requests); })
				.isInstanceOf(BulkSaveOperationException.class)
				.hasCauseInstanceOf(FileSaveException.class);

			// confirm that the files are not present
			assertThat(Files.exists(tempDir.resolve("red.png"))).isFalse();
			assertThat(Files.exists(tempDir.resolve("blue.png"))).isFalse();
			assertThat(Files.exists(tempDir.resolve("green.png"))).isFalse();
		}
	}

	@Nested
	class DeleteFile {
		@Test
		void shouldDeleteFileWhenRelativePathIsProvided() throws Exception {
			MultipartFile redFile = TestStorageFactory.imageFile("red", "png", Color.RED);

			Path redPath = TestStorageFactory.writeFileToDisk(redFile, "red.png", tempDir);
			assertThat(Files.exists(redPath)).isTrue();

			storageService.delete("./red.png");
			assertThat(Files.exists(redPath)).isFalse();
		}

		@Test
		void shouldDeleteFileWhenProvidedRelativePathIsTheFilename() throws Exception {
			MultipartFile file = TestStorageFactory.imageFile("red", "png", Color.RED);

			Path saved = TestStorageFactory.writeFileToDisk(file, "red.png", tempDir);
			assertThat(Files.exists(saved)).isTrue();

			storageService.delete("red.png");
			assertThat(Files.exists(saved)).isFalse();
		}

		@Test
		void shouldDeleteFilesWhenRelativePathListIsProvided() throws Exception {
			MultipartFile redFile = TestStorageFactory.imageFile("red", "png", Color.RED);
			MultipartFile blueFile = TestStorageFactory.imageFile("blue", "png", Color.BLUE);
			MultipartFile greenFile = TestStorageFactory.imageFile("green", "png", Color.GREEN);

			// write files to disk directly and verify they exist

			Path redPath = TestStorageFactory.writeFileToDisk(redFile, "red.png", tempDir);
			assertThat(Files.exists(redPath)).isTrue();

			Path bluePath = TestStorageFactory.writeFileToDisk(blueFile, "blue.png", tempDir);
			assertThat(Files.exists(bluePath)).isTrue();

			Path greenPath = TestStorageFactory.writeFileToDisk(greenFile, "green.png", tempDir);
			assertThat(Files.exists(greenPath)).isTrue();

			storageService.deleteAll(List.of("./red.png", "./blue.png", "./green.png"));
			assertThat(Files.exists(redPath)).isFalse();
			assertThat(Files.exists(bluePath)).isFalse();
			assertThat(Files.exists(greenPath)).isFalse();
		}

		@Test
		void shouldDeleteFilesWhenRelativePathListProvidedAreFilenames() throws Exception {
			MultipartFile redFile = TestStorageFactory.imageFile("red", "png", Color.RED);
			MultipartFile blueFile = TestStorageFactory.imageFile("blue", "png", Color.BLUE);
			MultipartFile greenFile = TestStorageFactory.imageFile("green", "png", Color.GREEN);

			// write files to disk directly and verify they exist

			Path redPath = TestStorageFactory.writeFileToDisk(redFile, "red.png", tempDir);
			assertThat(Files.exists(redPath)).isTrue();

			Path bluePath = TestStorageFactory.writeFileToDisk(blueFile, "blue.png", tempDir);
			assertThat(Files.exists(bluePath)).isTrue();

			Path greenPath = TestStorageFactory.writeFileToDisk(greenFile, "green.png", tempDir);
			assertThat(Files.exists(greenPath)).isTrue();

			storageService.deleteAll(List.of("red.png", "blue.png", "green.png"));
			assertThat(Files.exists(redPath)).isFalse();
			assertThat(Files.exists(bluePath)).isFalse();
			assertThat(Files.exists(greenPath)).isFalse();
		}
	}

	@Nested
	class GetFile {
		@Test
		void shouldReturnFile() throws IOException {
			MultipartFile imageFile = TestStorageFactory.imageFile("red", "png", Color.RED);
			Path saved = TestStorageFactory.writeFileToDisk(imageFile, "red.png", tempDir);
			assertThat(Files.exists(saved)).isTrue();

			Resource res = storageService.getFileAsResource("red.png");
			assertThat(res).isNotNull();
			assertThat(res.isFile()).isTrue();

			// basic integrity check
			File readBack = res.getFile();
			assertThat(readBack.length()).isEqualTo(imageFile.getSize());
			assertThat(readBack.getName()).isEqualTo("red.png");
		}

		@Test
		void shouldReturnNullWhenFileDoesNotExist() throws IOException {
			assertThatThrownBy(() -> storageService.getFileAsResource("nonExistentFile.png"))
				.isInstanceOf(FileNotFoundException.class);
		}

		@Test
		void shouldReturnMultipleFiles() throws IOException {
			MultipartFile imageFile = TestStorageFactory.imageFile("red", "png", Color.RED);
			Path saved = TestStorageFactory.writeFileToDisk(imageFile, "red.png", tempDir);
			assertThat(Files.exists(saved)).isTrue();

			MultipartFile imageFile2 = TestStorageFactory.imageFile("blue", "png", Color.BLUE);
			Path saved2 = TestStorageFactory.writeFileToDisk(imageFile2, "blue.png", tempDir);
			assertThat(Files.exists(saved2)).isTrue();

			List<Resource> resources = storageService.getFilesAsResources(List.of("red.png", "blue.png"));
			assertThat(resources).hasSize(2);

			Resource res1 = resources.get(0);
			assertThat(res1).isNotNull();
			assertThat(res1.isFile()).isTrue();
			assertThat(res1.getFile().getName()).isEqualTo("red.png");

			Resource res2 = resources.get(1);
			assertThat(res2).isNotNull();
			assertThat(res2.isFile()).isTrue();
			assertThat(res2.getFile().getName()).isEqualTo("blue.png");
		}
	}
}
