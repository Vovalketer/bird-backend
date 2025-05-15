package com.gray.bird.media;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gray.bird.media.dto.ExtractedMetadata;
import com.gray.bird.media.dto.MediaDto;
import com.gray.bird.media.dto.MediaMetadata;
import com.gray.bird.media.dto.request.MediaRequest;
import com.gray.bird.media.dto.response.MediaFile;
import com.gray.bird.media.exception.MediaDatabaseException;
import com.gray.bird.media.exception.MediaNotFoundException;
import com.gray.bird.media.exception.MediaProcessingException;
import com.gray.bird.media.utils.FilenameGenerator;
import com.gray.bird.media.utils.MediaMetadataUtils;
import com.gray.bird.storage.StorageService;
import com.gray.bird.storage.dto.StorageRequest;
import com.gray.bird.storage.dto.StorageResult;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MediaService {
	@Qualifier("mediaStorageServiceBean")
	private final StorageService storageService;
	private final MediaRepository mediaRepository;
	private final MediaMapper mediaMapper;
	private final MediaMetadataUtils metadataUtils;
	private final FilenameGenerator filenameGenerator;
	private final MediaRequestValidator validator;

	@Transactional(readOnly = false)
	public List<MediaDto> uploadImages(UUID userId, Long postId, MediaRequest request) {
		if (request == null || request.content().isEmpty()) {
			return Collections.emptyList();
		}
		validator.validateRequestConstraints(request);

		Map<String, MediaMetadata> metadata = toMediaMetadata(request);
		validator.validateMediaMetadataConstraints(metadata.values());
		List<StorageRequest> toSave = toStorageRequests(request, metadata);

		List<StorageResult> saved = storageService.saveAll(toSave);

		List<MediaEntity> toPersist = toMediaEntities(userId, postId, saved, metadata);
		try {
			List<MediaEntity> persisted = mediaRepository.saveAll(toPersist);

			return persisted.stream()
				.map(mediaMapper::toMediaDto)
				.sorted(Comparator.comparing(MediaDto::sortOrder))
				.collect(Collectors.toList());
		} catch (DataAccessException e) {
			rollback(saved);
			throw new MediaDatabaseException(e);
		}
	}

	public MediaFile getMedia(String filename) {
		System.out.println("Filename: " + filename);
		MediaDto metadata = mediaRepository.findByFilename(filename, MediaDto.class)
								.orElseThrow(() -> new MediaNotFoundException(filename));
		Resource resource = storageService.getFileAsResource(metadata.relativePath());

		return new MediaFile(resource, metadata.originalFilename(), metadata.mimeType(), metadata.fileSize());
	}

	private List<MediaEntity> toMediaEntities(UUID userId, Long postId, List<StorageResult> storedMedia,
		Map<String, MediaMetadata> processedMedia) {
		return storedMedia.stream()
			.map(s -> {
				MediaMetadata metadata = processedMedia.get(s.originalFilename());
				if (metadata == null) {
					throw new MediaProcessingException(s.originalFilename());
				}
				return MediaEntity.builder()
					.postId(postId)
					.userId(userId)
					.sortOrder(metadata.sortOrder())
					.filename(s.storageFilename())
					.originalFilename(s.originalFilename())
					.relativePath(s.relativePath())
					.mimeType(metadata.extractedMetadata().mimeType())
					.fileSize(s.fileSize())
					.width(metadata.extractedMetadata().width())
					.height(metadata.extractedMetadata().height())
					.alt(metadata.userProvidedMetadata().map(m -> m.alt()).orElse(null))
					.build();
			})
			.collect(Collectors.toList());
	}

	private Map<String, MediaMetadata> toMediaMetadata(MediaRequest request) {
		return request.content()
			.stream()
			.map(content -> {
				MultipartFile file = content.file();
				ExtractedMetadata metadata;
				try (InputStream inputStream = file.getInputStream()) {
					metadata = metadataUtils.extractMetadata(inputStream, file.getOriginalFilename());
				} catch (IOException e) {
					throw new MediaProcessingException(file.getOriginalFilename(), e);
				}
				return new MediaMetadata(
					file.getOriginalFilename(), content.fileIndex(), content.metadata(), metadata);
			})
			.collect(Collectors.toMap(m -> m.originalFilename(), m -> m));
	}

	private List<StorageRequest> toStorageRequests(
		MediaRequest request, Map<String, MediaMetadata> metadata) {
		return request.content()
			.stream()
			.map(r -> {
				try {
					String originalFilename = r.file().getOriginalFilename();
					String extension = metadata.get(originalFilename).extractedMetadata().extension();
					return StorageRequest.builder()
						.fileStream(r.file().getInputStream())
						.originalFilename(originalFilename)
						.targetFilename(filenameGenerator.generateFilename(extension))
						.fileSize(r.file().getSize())
						.build();
				} catch (IOException e) {
					throw new MediaProcessingException(r.file().getOriginalFilename(), e);
				}
			})
			.toList();
	}

	private void rollback(List<StorageResult> storedResults) {
		// the operation is transactional so the db rollback is automatic
		// the storage service doesnt have a way to rollback automatically so  we have to delete the files
		// manually
		storageService.deleteAll(storedResults.stream().map(s -> s.relativePath()).toList());
	}
}
