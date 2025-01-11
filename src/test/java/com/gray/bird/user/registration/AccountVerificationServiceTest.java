package com.gray.bird.user.registration;

import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.gray.bird.exception.InvalidConfirmationTokenException;
import com.gray.bird.user.registration.event.AccountVerificationEventPublisher;

@SpringJUnitConfig
public class AccountVerificationServiceTest {
	@Mock
	private AccountVerificationTokenRepository tokenRepository;
	@Mock
	private AccountVerificationEventPublisher publisher;

	@InjectMocks
	private AccountVerificationService accountVerificationService;

	@Test
	void testCreateVerificationToken() {
		UUID userId = UUID.randomUUID();
		AccountVerificationTokenEntity token =
			new AccountVerificationTokenEntity(userId, LocalDateTime.now().plusSeconds(300));
		Mockito.when(tokenRepository.save(Mockito.any(AccountVerificationTokenEntity.class)))
			.thenReturn(token);

		String verificationToken = accountVerificationService.createVerificationToken(userId);

		Assertions.assertThat(verificationToken).isNotNull();
		Assertions.assertThat(verificationToken).isEqualTo(token.getToken());
		Mockito.verify(tokenRepository, Mockito.times(1))
			.save(Mockito.any(AccountVerificationTokenEntity.class));
	}

	@Test
	void testVerifyAccount() {
		String tokenParam = "token";
		AccountVerificationTokenEntity token =
			new AccountVerificationTokenEntity(UUID.randomUUID(), LocalDateTime.now().plusMinutes(300L));
		Mockito.when(tokenRepository.findByToken(tokenParam)).thenReturn(Optional.of(token));
		Mockito.doNothing().when(publisher).accountVerified(Mockito.any(UUID.class));

		accountVerificationService.verifyAccount(tokenParam);

		Mockito.verify(tokenRepository, Mockito.times(1)).findByToken(tokenParam);
		Mockito.verify(publisher, Mockito.times(1)).accountVerified(token.getUserId());
	}

	@Test
	void whenVerificationTokenIsExpiredThrows() {
		String tokenParam = "token";
		AccountVerificationTokenEntity token =
			new AccountVerificationTokenEntity(UUID.randomUUID(), LocalDateTime.now().minusSeconds(300L));

		Mockito.when(tokenRepository.findByToken(tokenParam)).thenReturn(Optional.of(token));
		Mockito.doThrow(new InvalidConfirmationTokenException())
			.when(publisher)
			.accountVerified(token.getUserId());
	}
}
