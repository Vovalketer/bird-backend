package com.gray.bird.user.registration;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.gray.bird.user.UserEntity;

public interface ConfirmationRepository extends JpaRepository<Confirmation, Long> {
	Optional<Confirmation> findByToken(String token);

	Optional<Confirmation> findByUser(UserEntity user);
}
