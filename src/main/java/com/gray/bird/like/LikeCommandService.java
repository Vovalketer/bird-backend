package com.gray.bird.like;

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
public class LikeCommandService {
	private final LikeRepository repo;
	private final UserService userService;
	private final PostCommandService postService;
	private final AuthService authService;

	public void likePost(Long postId) {
		System.out.println("in LikePost function");
		String principalUsername = authService.getPrincipalUsername();
		UserEntity user = userService.getUserEntityByUsername(principalUsername);
		PostEntity post = postService.getByPostId(postId);
		LikeEntity like = new LikeEntity(user, post);
		System.out.println(like.toString());
		repo.save(like);
	}

	public void unlikePost(Long postId) {
		String principalUsername = authService.getPrincipalUsername();
		UserEntity user = userService.getUserEntityByUsername(principalUsername);
		PostEntity post = postService.getByPostId(postId);
		LikeEntity like = new LikeEntity(user, post);
		LikeId likeId = new LikeId(user.getId(), post.getId());
		repo.deleteById(likeId);
		// repo.delete(like);
	}
}
