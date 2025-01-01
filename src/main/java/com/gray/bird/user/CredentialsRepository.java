package com.gray.bird.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * CredentialRepository
 */
public interface CredentialsRepository extends JpaRepository<CredentialsEntity, Long> {
	Optional<CredentialsEntity> findCredentialsByUserUsernameIgnoreCase(String username);
	Optional<CredentialsEntity> findCredentialsByUserEmailIgnoreCase(String email);
	Optional<CredentialsEntity> findCredentialsByUserReferenceId(String referenceId);
}
