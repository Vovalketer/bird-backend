package com.gray.bird.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface ViewRepository<T, ID> extends Repository<T, ID> {
	List<T> findAll();

	List<T> findAll(Sort sort);

	Page<T> findAll(Pageable pageable);

	List<T> findAllByIdIn(Iterable<ID> ids);

	Page<T> findAllByIdIn(Iterable<ID> ids, Pageable pageable);

	Optional<T> findById(ID id);

	long count();
}
