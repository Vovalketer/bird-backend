package com.gray.bird.storage;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.gray.bird.storage.dto.StorageRequest;
import com.gray.bird.storage.dto.StorageResult;
import com.gray.bird.storage.exception.BulkSaveOperationException;
import com.gray.bird.storage.exception.DirectoryCreationException;
import com.gray.bird.storage.exception.EmptyFileException;
import com.gray.bird.storage.exception.EmptyFilenameException;
import com.gray.bird.storage.exception.FileDeleteException;
import com.gray.bird.storage.exception.FileNotFoundException;
import com.gray.bird.storage.exception.FileSaveException;
import com.gray.bird.storage.exception.FilenameAlreadyExistsException;
import com.gray.bird.storage.exception.InvalidPathException;
import com.gray.bird.storage.exception.StorageException;

public class StorageService {
	private final Path basePath;

	public StorageService(Path basePath) {
		this.basePath = basePath.toAbsolutePath().normalize();
		try {
			Files.createDirectories(basePath);
		} catch (Exception e) {
			throw new DirectoryCreationException(basePath.toString(), e);
		}
	}

	public Path getPath(String relativePath) {
		return resolve(relativePath);
	}

	public Resource getFileAsResource(String relativePath) {
		Path resolvedPath = resolve(relativePath);
		// isRegularFile also performs an existence check
		if (!Files.isRegularFile(resolvedPath)) {
			throw new FileNotFoundException(relativePath);
		}
		return new FileSystemResource(resolvedPath.toFile());
	}

	public List<Resource> getFilesAsResources(Collection<String> relativePaths) {
		return relativePaths.stream().map(this::getFileAsResource).toList();
	}

	public StorageResult save(StorageRequest request) throws FileSaveException {
		if (request.originalFilename() == null || request.originalFilename().isEmpty()) {
			throw new EmptyFilenameException();
		}
		if (request.targetFilename() == null || request.targetFilename().isEmpty()) {
			throw new EmptyFilenameException();
		}

		try (BufferedInputStream bufferedStream = getBufferedInputStream(request.fileStream())) {
			if (isStreamEmpty(bufferedStream)) {
				throw new EmptyFileException(request.targetFilename());
			}

			Path targetLocation = buildPath(request.targetFilename(), request.directory());

			Files.copy(bufferedStream, targetLocation);

			return StorageResult.builder()
				.storageFilename(request.targetFilename())
				.originalFilename(request.originalFilename())
				.extension(extractExtension(targetLocation))
				.fileSize(request.fileSize())
				.relativePath(relativize(targetLocation))
				.fileResource(new FileSystemResource(targetLocation.toAbsolutePath().toString()))
				.build();
		} catch (FileAlreadyExistsException e) {
			throw new FilenameAlreadyExistsException(request.targetFilename());
		} catch (IOException e) {
			throw new FileSaveException(request.targetFilename(), e);
		}
	}

	/**
	 * If a file fails to save, the operation is rolled back and the files that have been saved so far are
	 * deleted. An exception is thrown.
	 * @param request - a colllection of requests containing an input stream, original filename, target
	 *     filename, directory, and file size
	 * @return a list containing a resource, relative path and associated metadata
	 * @throws FileSaveException
	 */
	public List<StorageResult> saveAll(Collection<StorageRequest> request) throws FileSaveException {
		List<StorageResult> results = new ArrayList<>();
		request.forEach(r -> {
			try {
				StorageResult saved = save(r);
				results.add(saved);
			} catch (StorageException e) {
				for (StorageResult d : results) {
					try {
						delete(d.relativePath());
					} catch (FileDeleteException ignored) {
					}
				}
				throw new BulkSaveOperationException(e);
			}
		});
		return results;
	}

	public void delete(String relativePath) throws FileDeleteException {
		Path path = resolve(relativePath);
		if (!path.startsWith(basePath)) {
			throw new InvalidPathException(relativePath);
		}
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			throw new FileDeleteException(relativePath, e);
		}
	}

	public void deleteAll(Collection<String> relativePaths) throws FileDeleteException {
		for (String relativePath : relativePaths) {
			delete(relativePath);
		}
	}

	private Path resolve(String relativePath) {
		Path path = null;
		try {
			path = basePath.resolve(relativePath).normalize();

		} catch (java.nio.file.InvalidPathException e) {
			throw new InvalidPathException(relativePath);
		}
		validatePath(path);
		return path;
	}

	private String relativize(Path path) {
		validatePath(path);
		return basePath.relativize(path.toAbsolutePath().normalize()).toString();
	}

	private Path buildPath(String filename, String directory) {
		if (filename == null || filename.isEmpty()) {
			throw new EmptyFilenameException();
		}
		Path path = null;
		if (directory == null || directory.isEmpty()) {
			path = Path.of(filename);
		} else {
			path = Path.of(directory, filename);
		}
		try {
			Path target = basePath.resolve(path).normalize();
			validatePath(target);
			return target;
		} catch (java.nio.file.InvalidPathException e) {
			throw new InvalidPathException(path.toString());
		}
	}

	private void validatePath(Path path) {
		if (!path.startsWith(basePath)) {
			throw new InvalidPathException(path.toString());
		}
	}

	private String extractExtension(Path path) {
		String filename = path.getFileName().toString();
		int lastDot = filename.lastIndexOf('.');
		if (lastDot == -1) {
			return "";
		}
		return filename.substring(lastDot);
	}

	// peek one byte from the stream to determine if its empty
	// avaliable() might return 0 even if the stream is not empty
	private boolean isStreamEmpty(BufferedInputStream stream) throws IOException {
		if (stream == null) {
			return true;
		}
		stream.mark(1);
		int read = stream.read();
		stream.reset();

		return read == -1;
	}

	private BufferedInputStream getBufferedInputStream(InputStream stream) {
		// wrap in a buffered stream if the stream is not already buffered
		// the new instance of BufferedInputStream will point to the same underlying stream
		// so we dont need to close the plain stream after creating a new buffered stream
		return stream instanceof BufferedInputStream ? (BufferedInputStream) stream
													 : new BufferedInputStream(stream);
	}
}
