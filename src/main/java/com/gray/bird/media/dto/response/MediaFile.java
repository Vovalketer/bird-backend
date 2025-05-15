package com.gray.bird.media.dto.response;

import org.springframework.core.io.Resource;

public record MediaFile(Resource resource, String originalFilename, String contentType, long fileSize) {
}
