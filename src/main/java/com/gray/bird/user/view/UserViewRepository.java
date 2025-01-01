package com.gray.bird.user.view;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import com.gray.bird.common.ViewRepository;

public interface UserViewRepository extends ViewRepository<UserView, Long> {
	Optional<UserView> findByUsername(String username);

	@Query("SELECT u.id FROM UserView u WHERE u.username = :username")
	Optional<Long> findUserIdByUsername(@Param("username") String username);
}
