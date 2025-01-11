package com.gray.bird.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import com.gray.bird.post.dto.RepliesCount;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
	<T> Optional<T> findById(Long id, Class<T> type);
	<T> List<T> findAllByIdIn(Iterable<Long> id, Class<T> type);
	Page<Long> findRepliesByParentPostId(Long postId, Pageable pageable);

	@Query("SELECT new com.gray.bird.post.dto.RepliesCount(p.id, COUNT(p)) FROM PostEntity p WHERE "
		   + "p.parentPostId = :id")
	Optional<RepliesCount>
	countRepliesByPostId(@Param("id") Long id);

	@Query("SELECT new com.gray.bird.post.dto.RepliesCount(p.id, COUNT(p)) FROM PostEntity p WHERE "
		   + "p.parentPostId IN :ids")
	List<RepliesCount>
	countRepliesByPostIdsIn(@Param("ids") Iterable<Long> ids);

	@Query("SELECT p.id FROM PostEntity p WHERE p.userId = :userId")
	Page<Long> findPostIdsByUserId(@Param("userId") Long userId, Pageable pageable);
}
