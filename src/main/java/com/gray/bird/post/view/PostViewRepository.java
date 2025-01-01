package com.gray.bird.post.view;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.gray.bird.common.ViewRepository;

public interface PostViewRepository extends ViewRepository<PostView, Long> {
	Page<PostView> findByUserId(Long userId, Pageable pageable);

	Page<PostView> findByParentPostId(Long postId, Pageable pageable);

	Page<PostView> findByUserReferenceId(String referenceId, Pageable pageable);

	@Query("SELECT t FROM PostView t LEFT JOIN RepostEntity r ON t.userId = r.user.id "
			+ "WHERE r.user.id = :userId OR t.userId = :userId "
			+ "ORDER BY CASE WHEN r.repostedAt > t.createdAt THEN r.repostedAt " + "ELSE t.createdAt END DESC")
	Page<PostView> findUserTimelineByUserId(@Param("userId") Long userId, Pageable pageable);

	@Query("SELECT t FROM PostView t WHERE t.parentPostId = :parentPostId ")
	Page<PostView> findRepliesByParentId(@Param("parentPostId") Long parentPostId, Pageable pageable);

	@Query("SELECT t.id FROM PostView t WHERE t.parentPostId = :parentPostId ")
	List<Long> findRepliesIdByParentId(@Param("parentPostId") Long parentPostId);

	@Query("SELECT t.id FROM PostView t WHERE t.parentPostId = :parentPostId ")
	Page<Long> findRepliesIdByParentId(@Param("parentPostId") Long parentPostId, Pageable pageable);
}
