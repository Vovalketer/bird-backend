package com.gray.bird.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

import com.gray.bird.storage.StorageService;

@Configuration
public class MediaConfig {
	@Bean(name = "mediaStorageServiceBean")
	public StorageService mediaStorageService(@Value("${storage.media-path}") String mediaPath) {
		return new StorageService(Paths.get(mediaPath));
	}
}
