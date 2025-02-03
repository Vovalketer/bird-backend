package com.gray.bird.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByEmailIgnoreCase(String email);

	Optional<UserEntity> findByUsernameIgnoreCase(String username);
	<T> Optional<T> findByUsernameIgnoreCase(String username, Class<T> type);

	Optional<UserEntity> findByUuid(UUID uuid);

	@Query("SELECT u.uuid FROM UserEntity u WHERE u.username = :username")
	Optional<UUID> findUuidByUsername(@Param("username") String username);

	<T> Optional<T> findById(Long id, Class<T> type);
	<T> Optional<T> findByUuid(UUID id, Class<T> type);

	<T> List<T> findAllByIdIn(Iterable<Long> ids, Class<T> type);

	<T> List<T> findAllByUuidIn(Iterable<UUID> ids, Class<T> type);

	@Query("SELECT u.id FROM UserEntity u WHERE u.username = :username")
	Optional<Long> findIdByUsername(@Param("username") String username);
}
