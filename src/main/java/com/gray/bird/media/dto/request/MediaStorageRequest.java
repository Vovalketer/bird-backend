package com.gray.bird.media.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record MediaStorageRequest(int sortOrder, String originalFilename, MultipartFile file) {
}
