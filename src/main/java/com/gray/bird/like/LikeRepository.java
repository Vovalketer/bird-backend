package com.gray.bird.like;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, LikeId> {
	@Query("SELECT l.id.postId FROM LikeEntity l WHERE l.id.userId = :userId")
	Page<Long> findLikedPostsByUserId(@Param("userId") Long userId, Pageable pageable);

	@Query("SELECT l.id.userId FROM LikeEntity l WHERE l.id.postId = :postId")
	Page<Long> findUsersLikingPostId(@Param("postId") Long postId, Pageable pageable);

	Optional<Long> countByPostId(Long postId);
}
