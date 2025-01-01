package com.gray.bird.repost;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.gray.bird.auth.AuthService;
import com.gray.bird.post.PostCommandService;
import com.gray.bird.post.PostEntity;
import com.gray.bird.user.UserEntity;
import com.gray.bird.user.UserService;

@Service
@RequiredArgsConstructor
@Transactional
public class RepostManager {
	private final UserService userService;
	private final PostCommandService postService;
	private final AuthService authService;
	private final RepostRepository repo;

	public void repost(Long postId) {
		String principalUsername = authService.getPrincipalUsername();
		PostEntity post = postService.getByPostId(postId);
		UserEntity user = userService.getUserEntityByUsername(principalUsername);
		RepostEntity repost = new RepostEntity(user, post);
		repo.save(repost);
	}

	public void unrepost(Long postId) {
		String principalUsername = authService.getPrincipalUsername();
		PostEntity post = postService.getByPostId(postId);
		UserEntity user = userService.getUserEntityByUsername(principalUsername);
		RepostEntity repost = new RepostEntity(user, post);
		repo.delete(repost);
	}
}
