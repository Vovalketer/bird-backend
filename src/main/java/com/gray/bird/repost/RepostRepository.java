package com.gray.bird.repost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.gray.bird.repost.dto.RepostsCount;

public interface RepostRepository extends JpaRepository<RepostEntity, RepostId> {
	@Query("SELECT r.id.postId FROM RepostEntity r WHERE r.id.userId = :userId")
	Page<Long> findRepostsByUserId(@Param("userId") UUID userid, Pageable pageable);

	@Query("SELECT r.id.userId FROM RepostEntity r WHERE r.id.postId = :postId")
	Page<Long> findUsersRepostingByPostId(@Param("postId") Long postId, Pageable pageable);

	@Query("SELECT new com.gray.bird.repost.dto.RepostsCount(r.id.postId, COUNT(r.id.userId)) FROM "
		+ "RepostEntity r "
		+ "WHERE r.id.postId = :postId "
		+ "GROUP BY r.id.postId ")
	Optional<RepostsCount>
	countByPostId(@Param("postId") Long postId);

	@Query("SELECT new com.gray.bird.repost.dto.RepostsCount(r.id.postId, COUNT(r.id.userId)) FROM "
		+ "RepostEntity r "
		+ "WHERE r.id.postId IN :postId "
		+ "GROUP BY r.id.postId ")
	List<RepostsCount>
	countByPostIdsIn(@Param("postId") Iterable<Long> postIds);

	@Query("SELECT r FROM RepostEntity r WHERE r.id.postId = :postId")
	List<RepostEntity> findByPostId(@Param("postId") Long postId);

	@Query("SELECT r FROM RepostEntity r WHERE r.id.postId IN :postId")
	List<RepostEntity> findByPostIdsIn(@Param("postIds") Iterable<Long> postIds);
}
