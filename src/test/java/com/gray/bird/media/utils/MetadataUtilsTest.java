package com.gray.bird.media.utils;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.gray.bird.media.exception.ExtensionMismatchException;
import com.gray.bird.media.exception.InvalidMetadataException;
import com.gray.bird.media.exception.MalformedContentTypeException;
import com.gray.bird.media.exception.MediaProcessingException;
import com.gray.bird.media.exception.UndefinedExtensionException;

@ExtendWith(SpringExtension.class)
public class MetadataUtilsTest {
	@Mock
	private AutoDetectParser parser;
	@Mock
	private MimeTypes mimeTypes;

	@InjectMocks
	private MediaMetadataUtils metadataUtils;

	InputStream dummyStream;

	@BeforeEach
	void setUp() {
		dummyStream = new ByteArrayInputStream("dummy".getBytes());
	}

	@Test
	void shouldThrowWhenParserCantProcessTheRequest() throws IOException, SAXException, TikaException {
		Mockito.doThrow(new IOException())
			.when(parser)
			.parse(Mockito.any(InputStream.class),
				Mockito.any(BodyContentHandler.class),
				Mockito.any(Metadata.class));

		Assertions.assertThatThrownBy(() -> metadataUtils.extractMetadata(dummyStream, "dummy"))
			.isInstanceOf(MediaProcessingException.class);
	}

	@Test
	void shouldThrowWhenRequestFilenameHasNoExtension() {
		Assertions.assertThatThrownBy(() -> metadataUtils.extractMetadata(dummyStream, "dummy"))
			.isInstanceOf(UndefinedExtensionException.class);
	}

	@Test
	void shouldThrowWhenExtensionDoesNotMatchMIMEType() {
		Assertions.assertThatThrownBy(() -> metadataUtils.extractMetadata(dummyStream, "dummy.jpg"))
			.isInstanceOf(ExtensionMismatchException.class);
	}

	@Test
	void shouldThrowWhenExtractingMetadataAndMIMETypeIsMalformed()
		throws IOException, SAXException, TikaException {
		Mockito
			.doAnswer(invocation -> {
				Metadata metadataArg = invocation.getArgument(2);
				metadataArg.set(Metadata.CONTENT_TYPE, "bad type");
				return null;
			})
			.when(parser)
			.parse(Mockito.any(InputStream.class),
				Mockito.any(BodyContentHandler.class),
				Mockito.any(Metadata.class));

		Mockito.when(mimeTypes.getRegisteredMimeType("bad type"))
			.thenThrow(new MimeTypeException("Exception"));

		Assertions.assertThatThrownBy(() -> metadataUtils.extractMetadata(dummyStream, "dummy.testextension"))
			.isInstanceOf(MalformedContentTypeException.class);
	}

	@Test
	void shouldThrowWhenIsValidIsCalledAndMIMETypeIsMalformed() throws MimeTypeException {
		Mockito.when(mimeTypes.getRegisteredMimeType("bad type"))
			.thenThrow(new MimeTypeException("Invalid type"));

		Assertions.assertThatThrownBy(() -> metadataUtils.isContentTypeValid("bad type", "testextension"))
			.isInstanceOf(MalformedContentTypeException.class);
	}

	@Test
	void shouldThrowWhenWidthIsMissing() throws IOException, SAXException, TikaException {
		Mockito
			.doAnswer(invocation -> {
				Metadata metadataArg = invocation.getArgument(2);
				metadataArg.set(Metadata.CONTENT_TYPE, "test");
				metadataArg.set(Metadata.IMAGE_WIDTH, "");
				return null;
			})
			.when(parser)
			.parse(Mockito.any(InputStream.class),
				Mockito.any(BodyContentHandler.class),
				Mockito.any(Metadata.class));

		MimeType mimeType = Mockito.mock(MimeType.class);
		Mockito.when(mimeTypes.getRegisteredMimeType(Mockito.anyString())).thenReturn(mimeType);
		Mockito.when(mimeType.getExtensions()).thenReturn(List.of(".testextension"));

		Assertions.assertThatThrownBy(() -> metadataUtils.extractMetadata(dummyStream, "dummy.testextension"))
			.isInstanceOf(InvalidMetadataException.class);
	}

	@Test
	void shouldThrowWhenHeightIsMissing() throws IOException, SAXException, TikaException {
		Mockito
			.doAnswer(invocation -> {
				Metadata metadataArg = invocation.getArgument(2);
				metadataArg.set(Metadata.CONTENT_TYPE, "test");
				metadataArg.set(Metadata.IMAGE_WIDTH, "1");
				metadataArg.set(Metadata.IMAGE_LENGTH, "");
				return null;
			})
			.when(parser)
			.parse(Mockito.any(InputStream.class),
				Mockito.any(BodyContentHandler.class),
				Mockito.any(Metadata.class));

		MimeType mimeType = Mockito.mock(MimeType.class);
		Mockito.when(mimeTypes.getRegisteredMimeType(Mockito.anyString())).thenReturn(mimeType);
		Mockito.when(mimeType.getExtensions()).thenReturn(List.of(".testextension"));

		Assertions.assertThatThrownBy(() -> metadataUtils.extractMetadata(dummyStream, "dummy.testextension"))
			.isInstanceOf(InvalidMetadataException.class);
	}

	@Test
	void shouldThrowWhenOrientationIsMissing() throws IOException, SAXException, TikaException {
		Mockito
			.doAnswer(invocation -> {
				Metadata metadataArg = invocation.getArgument(2);
				metadataArg.set(Metadata.CONTENT_TYPE, "test");
				metadataArg.set(Metadata.IMAGE_WIDTH, "1");
				metadataArg.set(Metadata.IMAGE_LENGTH, "1");
				metadataArg.set(Metadata.ORIENTATION, "");
				return null;
			})
			.when(parser)
			.parse(Mockito.any(InputStream.class),
				Mockito.any(BodyContentHandler.class),
				Mockito.any(Metadata.class));

		MimeType mimeType = Mockito.mock(MimeType.class);
		Mockito.when(mimeTypes.getRegisteredMimeType(Mockito.anyString())).thenReturn(mimeType);
		Mockito.when(mimeType.getExtensions()).thenReturn(List.of(".testextension"));

		Assertions.assertThatThrownBy(() -> metadataUtils.extractMetadata(dummyStream, "dummy.testextension"))
			.isInstanceOf(InvalidMetadataException.class);
	}
}
