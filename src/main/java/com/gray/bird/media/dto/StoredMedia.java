package com.gray.bird.media.dto;

import org.springframework.core.io.Resource;

public record StoredMedia(int sortOrder, String storageFilename, String originalFilename, Resource path) {
}
