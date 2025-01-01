package com.gray.bird.media;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface MediaRepository extends JpaRepository<MediaEntity, Long> {
	<T> List<T> findByPostId(Long id, Class<T> type);
	<T> List<T> findAllByPostIdIn(Iterable<Long> id, Class<T> type);
}
