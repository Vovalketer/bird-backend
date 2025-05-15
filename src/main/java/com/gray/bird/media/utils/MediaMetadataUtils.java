package com.gray.bird.media.utils;

import org.springframework.stereotype.Component;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import com.gray.bird.media.MediaConstants;
import com.gray.bird.media.dto.ExtractedMetadata;
import com.gray.bird.media.exception.ExtensionMismatchException;
import com.gray.bird.media.exception.InvalidMetadataException;
import com.gray.bird.media.exception.MalformedContentTypeException;
import com.gray.bird.media.exception.MediaException;
import com.gray.bird.media.exception.MediaProcessingException;
import com.gray.bird.media.exception.UndefinedExtensionException;

@Component
public class MediaMetadataUtils {
	private final AutoDetectParser parser;
	private final MimeTypes mimeTypes;

	public MediaMetadataUtils() {
		parser = new AutoDetectParser(getTikaConfig());
		mimeTypes = MimeTypes.getDefaultMimeTypes();
	}

	// test-friendly constructor
	MediaMetadataUtils(AutoDetectParser parser, MimeTypes mimeTypes) {
		this.parser = parser;
		this.mimeTypes = mimeTypes;
	}

	public ExtractedMetadata extractMetadata(InputStream stream, String filename) {
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		try {
			parser.parse(stream, handler, metadata);
		} catch (Exception e) {
			throw new MediaProcessingException(filename, e);
		}

		String contentType = metadata.get(Metadata.CONTENT_TYPE);
		String extension = getExtension(filename);
		if (!isContentTypeValid(contentType, extension)) {
			throw new ExtensionMismatchException(filename, contentType);
		}
		System.out.println("Content type: " + contentType + "extension: " + extension);
		int width;
		try {
			width = Integer.parseInt(metadata.get(Metadata.IMAGE_WIDTH));
		} catch (NullPointerException | NumberFormatException e) {
			throw new InvalidMetadataException("IMAGE_WIDTH", filename);
		}
		int height;
		try {
			height = Integer.parseInt(metadata.get(Metadata.IMAGE_LENGTH));
		} catch (NullPointerException | NumberFormatException e) {
			throw new InvalidMetadataException("IMAGE_LENGTH", filename);
		}

		int orientation = 0;
		String orientationString = metadata.get(Metadata.ORIENTATION);
		try {
			if (orientationString != null) {
				orientation = Integer.parseInt(orientationString);
			}
		} catch (NumberFormatException e) {
			throw new InvalidMetadataException("ORIENTATION", filename);
		}

		return ExtractedMetadata.builder()
			.filename(filename)
			.extension(extension)
			.mimeType(contentType)
			.width(width)
			.height(height)
			.orientation(orientation)
			.build();
	}

	private String getExtension(String filename) {
		int index = filename.lastIndexOf(".");
		if (index == -1) {
			throw new UndefinedExtensionException(filename);
		}
		return filename.substring(index);
	}

	public boolean isContentTypeValid(String contentType, String extension) {
		try {
			MimeType registeredMimeType = mimeTypes.getRegisteredMimeType(contentType);
			return registeredMimeType != null && registeredMimeType.getExtensions().contains(extension);
		} catch (MimeTypeException e) {
			throw new MalformedContentTypeException(contentType, e);
		}
	}

	public boolean isAllowedImageType(String mimeType) {
		return MediaConstants.ALLOWED_IMAGE_TYPES.contains(mimeType);
	}

	private TikaConfig getTikaConfig() {
		try {
			TikaConfig tikaConfig =
				new TikaConfig(getClass().getClassLoader().getResource("./tika-config.xml"));
			return tikaConfig;
		} catch (TikaException | IOException | SAXException e) {
			throw new MediaException("Error loading the media parser config file", e);
		}
	}
}
