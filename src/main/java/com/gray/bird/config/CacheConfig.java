package com.gray.bird.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.gray.bird.common.CacheNames;

@Configuration
@EnableCaching
public class CacheConfig {
	@Bean
	public CacheManager cacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager();
		cacheManager.registerCustomCache(CacheNames.LOGIN_ATTEMPTS_CACHE,
				Caffeine.newBuilder().expireAfterAccess(15, TimeUnit.MINUTES).maximumSize(100).build());

		return cacheManager;
	}
}
