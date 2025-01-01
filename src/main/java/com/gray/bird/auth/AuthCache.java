package com.gray.bird.auth;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.gray.bird.common.CacheNames;
import com.gray.bird.exception.CacheException;

@Service
@RequiredArgsConstructor
public class AuthCache {
	private final CacheManager cacheManager;

	public int getLoginAttempts(String email) {
		Cache cache = getCache();
		Integer attempts = cache.get(email, Integer.class);
		if (attempts != null) {
			return attempts;
		} else {
			return 0;
		}
	}

	public int incrementLoginAttempts(String email) {
		Cache cache = getCache();
		Integer attempts = cache.get(email, Integer.class);
		if (attempts != null) {
			++attempts;
			cache.put(email, attempts);
			return attempts;
		} else {
			cache.put(email, 1);
			return 1;
		}
	}

	public void clearLoginAttempts(String email) {
		Cache cache = getCache();
		cache.evict(email);
	}

	private Cache getCache() {
		Cache cache = cacheManager.getCache(CacheNames.LOGIN_ATTEMPTS_CACHE);
		if (cache != null) {
			return cache;
		}
		throw new CacheException("The cache " + CacheNames.LOGIN_ATTEMPTS_CACHE + " hasnt been initialized");
	}
}
