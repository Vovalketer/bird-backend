package com.gray.bird.user.registration;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.gray.bird.user.UserEntity;

public interface AccountVerificationTokenRepository
	extends JpaRepository<AccountVerificationTokenEntity, Long> {
	Optional<AccountVerificationTokenEntity> findByToken(String token);

	Optional<AccountVerificationTokenEntity> findByUser(UserEntity user);
}
