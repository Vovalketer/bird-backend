package com.gray.bird.storage;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

	public Path getPath(String path) {
		try {
			Path target = basePath.resolve(path).normalize();
			if (!target.startsWith(basePath)) {
				throw new InvalidPathException(path);
			}
			return target;
		} catch (java.nio.file.InvalidPathException e) {
			throw new InvalidPathException(path);
		}
	}

	public Resource getFileAsResource(String path) {
		Path resolvedPath = getPath(path);
		// isRegularFile also performs an existence check
		if (!Files.isRegularFile(resolvedPath)) {
			throw new FileNotFoundException(path);
		}
		return new FileSystemResource(resolvedPath.toFile());
	}

	public List<Resource> getFilesAsResources(Collection<String> paths) {
		return paths.stream().map(this::getFileAsResource).toList();
	}

	public Path save(String path, MultipartFile file) throws FileSaveException {
		if (file.isEmpty()) {
			throw new EmptyFileException(path);
		}
		if (path == null || path.isEmpty()) {
			throw new EmptyFilenameException();
		}

		Path targetLocation = getPath(path);
		try (InputStream inputStream = file.getInputStream()) {
			Files.copy(inputStream, targetLocation);
		} catch (FileAlreadyExistsException e) {
			throw new FilenameAlreadyExistsException(path);
		} catch (IOException e) {
			throw new FileSaveException(path, e);
		}

		return targetLocation;
	}

	/**
	 * Saves all files in a path/file map.
	 * if a file fails to save, the operation is rolled back and the files that have been saved so far are
	 * deleted and an exception is thrown
	 * @param files - a path/file map
	 * @return a list of paths
	 * @throws FileSaveException
	 */
	public List<Path> saveAll(Map<String, MultipartFile> files) throws FileSaveException {
		List<Path> savedFiles = new ArrayList<>();
		for (Map.Entry<String, MultipartFile> entry : files.entrySet()) {
			String filename = entry.getKey();
			MultipartFile file = entry.getValue();
			try {
				Path saved = save(filename, file);
				savedFiles.add(saved);
			} catch (StorageException e) {
				for (Path p : savedFiles) {
					try {
						delete(p);
					} catch (FileDeleteException ignored) {
					}
				}
				throw new BulkSaveOperationException(e);
			}
		};
		return savedFiles;
	}

	public void delete(Path path) throws FileDeleteException {
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			throw new FileDeleteException(path.getFileName().toString(), e);
		}
	}

	public void delete(String path) throws FileDeleteException {
		delete(getPath(path));
	}

	public void deleteAllByFilename(Collection<String> paths) throws FileDeleteException {
		for (String filename : paths) {
			delete(filename);
		}
	}

	public void deleteAllByPath(Collection<Path> paths) throws FileDeleteException {
		for (Path path : paths) {
			delete(path);
		}
	}
}
