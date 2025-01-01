package com.gray.bird.media.view;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.gray.bird.common.ViewRepository;

public interface MediaViewRepository extends ViewRepository<MediaView, Long> {
	List<MediaView> findByPostId(Long postId);

	@Query("SELECT m FROM MediaView m WHERE m.postId IN :postIds")
	List<MediaView> findAllByPostId(@Param("postIds") Iterable<Long> postIds);
}
