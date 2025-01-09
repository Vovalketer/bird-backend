package com.gray.bird.repost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RepostRepository extends JpaRepository<RepostEntity, RepostId> {
	@Query("SELECT r.id.postId FROM RepostEntity r WHERE r.id.userId = :userId")
	Page<Long> findRepostsByUserId(@Param("userId") UUID userid, Pageable pageable);

	@Query("SELECT r.id.userId FROM RepostEntity r WHERE r.id.postId = :postId")
	Page<Long> findUsersRepostingByPostId(@Param("postId") Long postId, Pageable pageable);

	Optional<Long> countByPostId(Long id);
}
