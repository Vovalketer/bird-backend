package com.gray.bird.utils;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;

import com.gray.bird.media.MediaEntity;
import com.gray.bird.media.dto.ExtractedMetadata;
import com.gray.bird.media.dto.MediaDto;
import com.gray.bird.media.dto.MediaMetadata;
import com.gray.bird.media.dto.request.MediaContentRequest;
import com.gray.bird.media.dto.request.MediaInputMetadataRequest;
import com.gray.bird.media.dto.request.MediaRequest;

public class TestMediaFactory {
	public static MediaRequest mediaRequestWithImages(List<String> origFilenames) {
		List<MediaContentRequest> content = new ArrayList<>();
		int sortIndex = 0;
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			for (String n : origFilenames) {
				BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
				ImageIO.write(image, "jpg", outputStream);
				byte[] imageBytes = outputStream.toByteArray();
				MockMultipartFile file = new MockMultipartFile(n, n, MediaType.IMAGE_JPEG_VALUE, imageBytes);
				outputStream.reset();
				content.add(new MediaContentRequest(sortIndex, file));
				sortIndex++;
			}
			return new MediaRequest(content);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static MediaRequest mediaRequestWithImages(String... origFilenames) {
		List<MediaContentRequest> content = new ArrayList<>();
		int sortIndex = 0;
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			for (String n : origFilenames) {
				BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
				ImageIO.write(image, "jpg", outputStream);
				byte[] imageBytes = outputStream.toByteArray();
				MockMultipartFile file = new MockMultipartFile(n, n, MediaType.IMAGE_JPEG_VALUE, imageBytes);
				outputStream.reset();
				content.add(new MediaContentRequest(
					sortIndex, file, Optional.of(new MediaInputMetadataRequest("alt" + sortIndex))));
				sortIndex++;
			}
			return new MediaRequest(content);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static MediaRequest emptyMediaRequest() {
		return new MediaRequest(List.of());
	}

	public static List<MediaEntity> mediaEntities(
		Long postId, UUID userId, String mimeType, Collection<String> origFilenames) {
		int sortOrder = 0;
		List<MediaEntity> mediaEntities = new ArrayList<>();

		for (String f : origFilenames) {
			String filename = UUID.randomUUID().toString();
			var e = MediaEntity.builder()
						.postId(postId)
						.userId(userId)
						.sortOrder(sortOrder)
						.filename(filename)
						.mimeType(mimeType)
						.relativePath("./" + filename)
						.fileSize((long) Math.random() * 1000)
						.originalFilename(f)
						.build();
			sortOrder++;
			mediaEntities.add(e);
		}
		return mediaEntities;
	}

	public static List<MediaDto> mediaDtos(
		Long postId, UUID userId, String mimeType, Collection<String> origFilenames) {
		int sortOrder = 0;
		List<MediaDto> mediaEntities = new ArrayList<>();

		for (String f : origFilenames) {
			var e = MediaDto.builder()
						.postId(postId)
						.userId(userId)
						.sortOrder(sortOrder)
						.mimeType(mimeType)
						.filename(UUID.randomUUID().toString())
						.originalFilename(f)
						.build();
			sortOrder++;
			mediaEntities.add(e);
		}
		return mediaEntities;
	}

	public static ExtractedMetadata extractedMetadata(String filename, String mimeType) {
		return ExtractedMetadata.builder()
			.filename(filename)
			.mimeType(mimeType)
			.width(10)
			.height(10)
			.orientation(0)
			.build();
	}

	public static MediaMetadata mediaMetadata(String filename, String mimeType) {
		return MediaMetadata.builder()
			.originalFilename(filename)
			.sortOrder(0)
			.userProvidedMetadata(Optional.empty())
			.extractedMetadata(extractedMetadata(filename, mimeType))
			.build();
	}

	public static MultipartFile image(String filename, String extension, String contentType, int width,
		int height, Color color) throws IOException {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setColor(color);
		g.fillRect(0, 0, width, height);
		g.dispose();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, extension.substring(1), baos);
		byte[] imageBytes = baos.toByteArray();

		return new MockMultipartFile(filename, filename + extension, contentType, imageBytes);
	}
}
