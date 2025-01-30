package com.gray.bird.like;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.gray.bird.like.dto.LikesCount;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, LikeId> {
	@Query("SELECT l.id.postId FROM LikeEntity l WHERE l.id.userId = :userId")
	Page<Long> findLikedPostsByUserId(@Param("userId") UUID userId, Pageable pageable);

	@Query("SELECT l.id.userId FROM LikeEntity l WHERE l.id.postId = :postId")
	Page<Long> findUsersLikingPostId(@Param("postId") Long postId, Pageable pageable);

	@Query("SELECT new com.gray.bird.like.dto.LikesCount(l.id.postId, COUNT(l.id.userId)) FROM LikeEntity l "
		+ "WHERE l.id.postId = :postId")
	Optional<LikesCount>
	countByPostId(@Param("postId") Long postId);

	@Query("SELECT new com.gray.bird.like.dto.LikesCount(l.id.postId, COUNT(l.id.userId)) FROM LikeEntity l "
		+ "WHERE l.id.postId IN :postIds "
		+ "GROUP BY l.id.postId ")
	List<LikesCount>
	countByPostIdsIn(@Param("postIds") Iterable<Long> postIds);
}
