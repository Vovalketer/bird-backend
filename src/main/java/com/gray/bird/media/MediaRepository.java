package com.gray.bird.media;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface MediaRepository extends JpaRepository<MediaEntity, Long> {
	<T> Optional<T> findByFilename(String filename, Class<T> type);
	<T> List<T> findByPostId(Long id, Class<T> type);
	<T> List<T> findAllByPostIdIn(Iterable<Long> id, Class<T> type);
}
