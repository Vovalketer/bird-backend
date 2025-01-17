package com.gray.bird.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class ObjectMapperConfig {
	@Bean
	public ObjectMapper objectMapperBuilder() {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
		builder.modules(new JavaTimeModule());
		builder.featuresToEnable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
		builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
			SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS,
			DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		builder.serializationInclusion(JsonInclude.Include.NON_NULL);

		return builder.build();
	}
}
