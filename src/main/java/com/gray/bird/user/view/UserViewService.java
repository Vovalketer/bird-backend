package com.gray.bird.user.view;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.gray.bird.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class UserViewService {
	private final UserViewRepository userViewRepository;

	public UserView getUserById(Long id) {
		return userViewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
	}

	public List<UserView> getAllUsersById(Iterable<Long> ids) {
		return userViewRepository.findAllByIdIn(ids);
	}
	public Page<UserView> getAllUsersById(Iterable<Long> ids, Pageable pageable) {
		return userViewRepository.findAllByIdIn(ids, pageable);
	}
	public UserView getUserByUsername(String username) {
		return userViewRepository.findByUsername(username).orElseThrow(
			() -> new ResourceNotFoundException());
	}

	public Long getUserIdByUsername(String username) {
		return userViewRepository.findUserIdByUsername(username).orElseThrow(
			() -> new ResourceNotFoundException());
	}
}
