package com.gray.bird.user;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;
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
	public List<UserProjection> getAllUsersById(Iterable<Long> userIds) {
		return userRepository.findAllByIdIn(userIds, UserProjection.class);
	}

	public Long getUserIdByUsername(String username) {
		return userRepository.findUserIdByUsername(username).orElseThrow(
			() -> new ResourceNotFoundException());
	}
	public List<UserProjection> getUsersFromPosts(List<PostAggregate> posts) {
		List<Long> ids = getUserIdsFromPosts(posts);
		return getAllUsersById(ids);
	}
	private List<Long> getUserIdsFromPosts(List<PostAggregate> posts) {
		return posts.stream().map(p -> p.post().userId()).collect(Collectors.toList());
	}
}
