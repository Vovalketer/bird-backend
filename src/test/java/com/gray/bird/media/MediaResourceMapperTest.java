package com.gray.bird.media;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import com.gray.bird.media.dto.MediaDto;
import com.gray.bird.media.dto.MediaResource;
import com.gray.bird.utils.TestMediaFactory;

@ExtendWith(SpringExtension.class)
public class MediaResourceMapperTest {
	@Mock
	private MediaUrlBuilder urlBuilder;
	@InjectMocks
	private MediaResourceMapper mapper;

	@Test
	void testToResource() {
		UUID userId = UUID.randomUUID();
		Long postId = 1L;
		Mockito.when(urlBuilder.buildMediaUrl(Mockito.anyString())).thenReturn("test.com");
		MediaDto mediaDto =
			TestMediaFactory.mediaDtos(postId, userId, "image/jpeg", List.of("testFile1.jpg")).get(0);

		MediaResource result = mapper.toResource(mediaDto);

		Assertions.assertThat(result.getId()).isEqualTo(mediaDto.id());
		Assertions.assertThat(result.getType()).isEqualTo("media");

		// relationships
		Assertions.assertThat(result.getRelationships().getUser().getData().getId())
			.isEqualTo(mediaDto.userId());
		Assertions.assertThat(result.getRelationships().getPost().getData().getId())
			.isEqualTo(mediaDto.postId());

		// attributes
		Assertions.assertThat(result.getAttributes().url()).isEqualTo("test.com");
		Assertions.assertThat(result.getAttributes().originalFilename())
			.isEqualTo(mediaDto.originalFilename());
		Assertions.assertThat(result.getAttributes().sortOrder()).isEqualTo(mediaDto.sortOrder());
		Assertions.assertThat(result.getAttributes().alt()).isEqualTo(mediaDto.alt());
		Assertions.assertThat(result.getAttributes().width()).isEqualTo(mediaDto.width());
		Assertions.assertThat(result.getAttributes().height()).isEqualTo(mediaDto.height());
		Assertions.assertThat(result.getAttributes().fileSize()).isEqualTo(mediaDto.fileSize());
		Assertions.assertThat(result.getAttributes().duration()).isEqualTo(mediaDto.duration());
		Assertions.assertThat(result.getAttributes().mimeType()).isEqualTo(mediaDto.mimeType());
	}
}
