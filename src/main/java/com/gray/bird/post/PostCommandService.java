package com.gray.bird.post;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import com.gray.bird.auth.AuthService;
import com.gray.bird.exception.ResourceNotFoundException;
import com.gray.bird.media.MediaCommandService;
import com.gray.bird.post.dto.PostDto;
import com.gray.bird.post.dto.PostRequest;
import com.gray.bird.user.UserService;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class PostCommandService {
	private final PostRepository postRepository;
	private final AuthService authService;
	private final UserService userService;
	private final MediaCommandService mediaService;
	private final PostMapper postMapper;

	// TODO: merge Post and PostAggregate, possibly separate it from the JPA entity
	// TODO: use weak references

	public PostEntity savePost(PostEntity post) {
		return postRepository.save(post);
	}

	public PostEntity getByPostId(Long postId) {
		return postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException());
	}

	public PostDto createPost(PostRequest postRequest) {
		String username = authService.getPrincipalUsername();
		UUID userId = userService.getUserIdByUsername(username);
		boolean hasMedia = hasMedia(postRequest);
		if (hasMedia) {
			mediaService.uploadImages(postRequest.media());
		}
		PostEntity post = createPostEntity(postRequest, userId, hasMedia);
		PostEntity savedPost = savePost(post);
		return postMapper.toPostDto(savedPost);
	}

	public PostDto createReply(PostRequest postRequest, Long parentPostId) {
		String username = authService.getPrincipalUsername();
		UUID userId = userService.getUserIdByUsername(username);
		PostEntity parent = getByPostId(parentPostId);
		boolean hasMedia = hasMedia(postRequest);
		if (hasMedia) {
			mediaService.uploadImages(postRequest.media());
		}
		PostEntity post = createPostEntity(postRequest, parent, userId, hasMedia);

		PostEntity savedPost = savePost(post);

		return postMapper.toPostDto(savedPost);
	}

	private PostEntity createPostEntity(PostRequest post, UUID userId, boolean hasMedia) {
		return PostEntity.builder()
			.text(post.text())
			.replyType(post.replyType())
			.userId(userId)
			.hasMedia(hasMedia)
			.build();
	}

	private PostEntity createPostEntity(PostRequest post, PostEntity parent, UUID userId, boolean hasMedia) {
		return PostEntity.builder()
			.text(post.text())
			.replyType(post.replyType())
			.userId(userId)
			.hasMedia(hasMedia)
			.build();
	}

	private boolean hasMedia(PostRequest postRequest) {
		return postRequest.media() != null;
	}
}
