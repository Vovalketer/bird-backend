package com.gray.bird.user.registration;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import com.gray.bird.exception.InvalidVerificationTokenException;
import com.gray.bird.user.registration.event.AccountVerificationEventPublisher;

@Service
@RequiredArgsConstructor
public class AccountVerificationService {
	private final AccountVerificationTokenRepository tokenRepository;
	private final AccountVerificationEventPublisher publisher;

	private static final Integer ACCOUNT_CONFIRMATION_EXPIRATION = 86400;

	public String createVerificationToken(UUID userId) {
		AccountVerificationTokenEntity token = new AccountVerificationTokenEntity(
			userId, LocalDateTime.now().plusSeconds(ACCOUNT_CONFIRMATION_EXPIRATION));
		AccountVerificationTokenEntity savedToken = tokenRepository.save(token);
		return savedToken.getToken();
	}

	public void verifyAccount(String token) {
		AccountVerificationTokenEntity tok =
			tokenRepository.findByToken(token).orElseThrow(() -> new InvalidVerificationTokenException());
		if (tok.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new InvalidVerificationTokenException();
		}

		publisher.accountVerified(tok.getUserId());
	}
}
