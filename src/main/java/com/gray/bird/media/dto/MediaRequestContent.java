package com.gray.bird.media.dto;

import org.springframework.web.multipart.MultipartFile;

public record MediaRequestContent(MultipartFile file, String alt) {
}
