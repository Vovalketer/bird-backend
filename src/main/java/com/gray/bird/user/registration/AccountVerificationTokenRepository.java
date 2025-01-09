package com.gray.bird.user.registration;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountVerificationTokenRepository
	extends JpaRepository<AccountVerificationTokenEntity, Long> {
	Optional<AccountVerificationTokenEntity> findByToken(String token);

	Optional<AccountVerificationTokenEntity> findByUserUuid(UUID userId);
}
