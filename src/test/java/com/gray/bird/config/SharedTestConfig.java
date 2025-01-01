package com.gray.bird.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.gray.bird.utils.TestUtils;
import com.gray.bird.utils.TestUtilsFactory;

@TestConfiguration
public class SharedTestConfig {
	@Bean
	public TestUtils testUtils() {
		return TestUtilsFactory.createTestUtils();
	}
}
