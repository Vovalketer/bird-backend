package com.gray.bird.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gray.bird.common.utils.ResourceFactory;
import com.gray.bird.postAggregate.PostResourceConverter;

@TestConfiguration
@Import(SharedTestConfig.class)
public class PostTestConfig {
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		return objectMapper;
	}

	@Bean
	ResourceFactory resourceFactory(ObjectMapper objectMapper) {
		return new ResourceFactory(objectMapper);
	}

	@Bean
	public PostResourceConverter postResourceConverter(ResourceFactory resourceFactory) {
		return new PostResourceConverter(resourceFactory);
	}
}
