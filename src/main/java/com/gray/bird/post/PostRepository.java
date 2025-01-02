package com.gray.bird.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
	<T> Optional<T> findById(Long id, Class<T> type);
	<T> List<T> findAllByIdIn(Iterable<Long> id, Class<T> type);
	Page<Long> findRepliesByParentPostId(Long postId, Pageable pageable);

	@Query("SELECT COUNT(*) FROM PostEntity p WHERE p.parentPostId = :id")
	Optional<Long> countRepliesByPostId(@Param("id") Long id);

	@Query("SELECT p.id FROM PostEntity p WHERE p.username = :username")
	Page<Long> findPostIdsByUsername(String username, Pageable pageable);
}
