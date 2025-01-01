package com.gray.bird.user.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gray.bird.auth.AuthCache;
import com.gray.bird.common.CacheNames;
import com.gray.bird.exception.CacheException;
import com.gray.bird.exception.GlobalExceptionHandler;

@Import(GlobalExceptionHandler.class)
@ExtendWith(MockitoExtension.class)
public class UserCacheTest {
	@Mock
	private CacheManager cacheManager;
	@Mock
	private Cache cache;

	@InjectMocks
	private AuthCache userCache;

	@BeforeEach
	private void setup() {
		Mockito.when(cacheManager.getCache(CacheNames.LOGIN_ATTEMPTS_CACHE)).thenReturn(cache);
	}

	@Test
	void testClearLoginAttempts() {
		Mockito.doNothing().when(cache).evict(ArgumentMatchers.anyString());

		userCache.clearLoginAttempts("email@valid.com");
	}

	@Test
	void testGetLoginAttempts() {
		int value = 1;
		String key = "email";
		Mockito.when(cache.get(key, Integer.class)).thenReturn(value);

		int loginAttempts = userCache.getLoginAttempts(key);

		Assertions.assertThat(loginAttempts).isNotNull();
		Assertions.assertThat(loginAttempts).isEqualTo(value);
	}

	@Test
	void testIncrementLoginAttempts() {
		int value = 1;
		String key = "email";
		Mockito.when(cache.get(key, Integer.class)).thenReturn(value);

		int incrementedValue = userCache.incrementLoginAttempts(key);

		Assertions.assertThat(incrementedValue).isEqualTo(value + 1);
	}

	@Test
	void throwsWhenNoValidCacheIsFound() {
		Mockito.when(cacheManager.getCache(CacheNames.LOGIN_ATTEMPTS_CACHE)).thenReturn(null);
		Assertions.assertThatThrownBy(() -> userCache.getLoginAttempts("email"))
			.isInstanceOf(CacheException.class);
	}
}
