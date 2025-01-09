package com.gray.bird.user;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gray.bird.exception.ResourceNotFoundException;
import com.gray.bird.postAggregate.PostAggregate;
import com.gray.bird.user.dto.UserProjection;

@Service
@RequiredArgsConstructor
public class UserQueryService {
	private final UserRepository userRepository;

	public UserProjection getUserById(Long id) {
		return userRepository.findById(id, UserProjection.class)
			.orElseThrow(() -> new ResourceNotFoundException());
	}

	public UserProjection getUserByUuid(UUID uuid) {
		return userRepository.findByUuid(uuid, UserProjection.class)
			.orElseThrow(() -> new ResourceNotFoundException());
	}

	public UserProjection getUserByUsername(String username) {
		return userRepository.findByUsernameIgnoreCase(username, UserProjection.class)
			.orElseThrow(() -> new ResourceNotFoundException());
	}

	public List<UserProjection> getAllUsersById(Iterable<UUID> userIds) {
		return userRepository.findAllByUuidIn(userIds, UserProjection.class);
	}

	public Long getUserIdByUsername(String username) {
		return userRepository.findIdByUsername(username).orElseThrow(() -> new ResourceNotFoundException());
	}

	public List<UserProjection> getUsersFromPosts(List<PostAggregate> posts) {
		List<UUID> ids = getUserIdsFromPosts(posts);
		return getAllUsersById(ids);
	}

	private List<UUID> getUserIdsFromPosts(List<PostAggregate> posts) {
		return posts.stream().map(p -> p.post().userId()).collect(Collectors.toList());
	}
}
