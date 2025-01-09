package com.gray.bird.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * CredentialRepository
 */
public interface CredentialsRepository extends JpaRepository<CredentialsEntity, Long> {
	Optional<CredentialsEntity> findCredentialsByUserUsernameIgnoreCase(String username);
	Optional<CredentialsEntity> findCredentialsByUserEmailIgnoreCase(String email);
	Optional<CredentialsEntity> findCredentialsByUserUuid(UUID uuid);
}
