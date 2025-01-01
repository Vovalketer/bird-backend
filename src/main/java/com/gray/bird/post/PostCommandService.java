package com.gray.bird.post;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

import com.gray.bird.auth.AuthService;
import com.gray.bird.exception.ResourceNotFoundException;
import com.gray.bird.media.MediaCommandService;
import com.gray.bird.media.MediaEntity;
import com.gray.bird.post.dto.PostDto;
import com.gray.bird.post.dto.PostRequest;
import com.gray.bird.user.UserEntity;
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
		UserEntity user = userService.getUserEntityByUsername(username);
		Set<MediaEntity> media = mediaService.uploadImages(postRequest.media());
		PostEntity post = createPostEntity(postRequest, user, media);
		PostEntity savedPost = savePost(post);
		return postMapper.toPostDto(savedPost);
	}

	public PostDto createReply(PostRequest postRequest, Long parentPostId) {
		String username = authService.getPrincipalUsername();
		UserEntity user = userService.getUserEntityByUsername(username);
		PostEntity parent = getByPostId(parentPostId);
		Set<MediaEntity> media = mediaService.uploadImages(postRequest.media());
		PostEntity post = createPostEntity(postRequest, parent, user, media);

		PostEntity savedPost = savePost(post);

		return postMapper.toPostDto(savedPost);
	}

	private PostEntity createPostEntity(PostRequest post, UserEntity user, Set<MediaEntity> media) {
		return PostEntity.builder()
			.text(post.text())
			.replyType(post.replyType())
			.user(user)
			.userReferenceId(user.getReferenceId())
			.media(media)
			.build();
	}

	private PostEntity createPostEntity(
		PostRequest post, PostEntity parent, UserEntity user, Set<MediaEntity> media) {
		return PostEntity.builder()
			.text(post.text())
			.replyType(post.replyType())
			.user(user)
			.userReferenceId(user.getReferenceId())
			.media(media)
			.parentPost(parent)
			.build();
	}
}
