package com.gray.bird;

import org.springframework.boot.SpringApplication;

import com.gray.bird.testConfig.TestcontainersConfig;

public class TestBirdApplication {
	public static void main(String[] args) {
		SpringApplication.from(BirdApplication::main).with(TestcontainersConfig.class).run(args);
	}
}
