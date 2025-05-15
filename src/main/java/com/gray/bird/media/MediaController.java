package com.gray.bird.media;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.gray.bird.common.ResourcePaths;
import com.gray.bird.media.dto.response.MediaFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(ResourcePaths.MEDIA)
public class MediaController {
	private final MediaService mediaService;

	@GetMapping("/{filename:.+}")
	public ResponseEntity<Resource> getMedia(@PathVariable String filename) {
		MediaFile media = mediaService.getMedia(filename);

		return ResponseEntity.ok().headers(getHeaders(media)).body(media.resource());
	}

	private HttpHeaders getHeaders(MediaFile media) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(media.contentType()));
		headers.setContentLength(media.fileSize());

		ContentDisposition contentDisposition =
			ContentDisposition.inline().filename(media.originalFilename()).build();
		headers.setContentDisposition(contentDisposition);

		return headers;
	}
}
