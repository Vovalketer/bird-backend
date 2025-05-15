package com.gray.bird.media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import com.gray.bird.auth.jwt.JwtService;
import com.gray.bird.common.ResourcePaths;
import com.gray.bird.testConfig.TestcontainersConfig;
import com.gray.bird.utils.TestMediaFactory;

@SpringBootTest
@Testcontainers
@Import(TestcontainersConfig.class)
@AutoConfigureMockMvc
public class MediaControllerIT {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MediaRepository mediaRepository;
	@MockitoBean
	private JwtService jwtService;

	static Path tempDir = Path.of("target/test-classes/temp");

	Long POST_ID = 1L;
	UUID USER_ID = UUID.randomUUID();

	// use this as authentication header
	private String accessToken = "Bearer accessToken";

	@DynamicPropertySource
	static void overrideStoragePath(DynamicPropertyRegistry registry) {
		registry.add("storage.media-path", () -> tempDir.toString());
	}

	@BeforeEach
	void setUp() {
		Authentication authentication = new TestingAuthenticationToken(USER_ID, "password", "USER");
		Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(true);
		Mockito.when(jwtService.getAuthenticationFromAccessToken(Mockito.anyString()))
			.thenReturn(authentication);
	}

	@AfterEach
	void tearDown() throws IOException {
		mediaRepository.deleteAll();
		// Mockito.reset(mediaRepository);
		Files.walk(tempDir).filter(p -> !p.equals(tempDir)).forEach(p -> p.toFile().delete());
	}

	@Test
	@Transactional
	@Rollback
	void shouldGetMedia() throws Exception {
		String filename = "testImage.jpg";
		String mimeType = "image/jpeg";
		int size = 50;
		MultipartFile image = TestMediaFactory.image(filename, ".jpg", mimeType, size, size, Color.RED);
		try (InputStream inputStream = image.getInputStream()) {
			Path res = tempDir.resolve(filename).normalize();
			Files.copy(inputStream, res, StandardCopyOption.REPLACE_EXISTING);
		}

		MediaEntity entity = MediaEntity.builder()
								 .postId(POST_ID)
								 .userId(USER_ID)
								 .relativePath("./testImage.jpg")
								 .filename(filename)
								 .originalFilename(filename)
								 .mimeType(mimeType)
								 .width(size)
								 .height(size)
								 .fileSize(image.getSize())
								 .sortOrder(0)
								 .build();
		mediaRepository.save(entity);

		mockMvc
			.perform(MockMvcRequestBuilders.get(ResourcePaths.MEDIA + "/" + filename)
					.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().bytes(image.getBytes()))
			.andExpect(MockMvcResultMatchers.content().contentType(MediaType.IMAGE_JPEG_VALUE))
			.andExpect(MockMvcResultMatchers.header().string(
				HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"testImage.jpg\""))
			.andExpect(MockMvcResultMatchers.header().string(
				HttpHeaders.CONTENT_LENGTH, String.valueOf(image.getSize())));
	}
}
