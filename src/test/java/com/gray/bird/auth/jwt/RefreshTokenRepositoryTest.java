package com.gray.bird.auth.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.RequiredArgsConstructor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@SpringBootTest
public class RefreshTokenRepositoryTest {
	@Autowired
	private RefreshTokenRepository tokenRepo;

	@Test
	void retrieveToken() {
		String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9."
			+ "eyJhdWQiOlsiQmlyZEFwcCJdLCJpYXQiOjE3MzI2ODczMjIsIm5iZiI6MTczMjY4NzMyMiwic"
			+ "3ViIjoidXNlckEiLCJleHAiOjE3MzM1ODczMjJ9.XvfvkCAeKedQJ6Y3-"
			+ "ZyJoEEY0jLjqOXBWDKZtP40J8JYHOwk4cEas1Ee2TQH826oPv_KS7GYiNf7Jb7fbJxsIg";
		Optional<RefreshTokenEntity> res = tokenRepo.findByToken(token);
		// write test

		Assertions.assertThat(res).isNotEmpty();
		Assertions.assertThat(res.get().getToken()).isEqualTo(token);
	}
}
