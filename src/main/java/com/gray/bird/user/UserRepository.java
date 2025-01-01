package com.gray.bird.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByEmailIgnoreCase(String email);

	Optional<UserEntity> findByUsernameIgnoreCase(String username);

	Optional<UserEntity> findByReferenceId(String referenceId);

	<T> Optional<T> findById(Long id, Class<T> type);

	<T> List<T> findAllByIdIn(Iterable<Long> ids, Class<T> type);

	@Query("SELECT u.id FROM UserEntity u WHERE u.username = :username")
	Optional<Long> findUserIdByUsername(@Param("username") String username);
}
