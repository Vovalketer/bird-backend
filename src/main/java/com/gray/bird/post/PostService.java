package com.gray.bird.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import com.gray.bird.auth.AuthService;
import com.gray.bird.exception.ResourceNotFoundException;
import com.gray.bird.media.MediaCommandService;
import com.gray.bird.post.dto.PostDto;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.post.dto.PostRequest;
import com.gray.bird.post.dto.RepliesCount;
import com.gray.bird.user.UserService;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class, readOnly = true)
@Slf4j
public class PostService {
	private final PostRepository postRepository;
	private final AuthService authService;
	private final UserService userService;
	private final MediaCommandService mediaService;
	private final PostMapper postMapper;

	@Transactional
	public PostEntity savePost(PostEntity post) {
		return postRepository.save(post);
	}

	public PostEntity getByPostId(Long postId) {
		return postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException());
	}

	@Transactional
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

	@Transactional
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

	public PostProjection getPostById(Long id) {
		return postRepository.findById(id, PostProjection.class)
			.orElseThrow(() -> new ResourceNotFoundException());
	}

	public List<PostProjection> getAllPostsById(Iterable<Long> ids) {
		return postRepository.findAllByIdIn(ids, PostProjection.class);
	}

	public Page<Long> getReplyIds(Long postId, Pageable pageable) {
		return postRepository.findRepliesByParentPostId(postId, pageable);
	}

	public RepliesCount getRepliesCountByPostId(Long id) {
		return postRepository.countRepliesByPostId(id).orElseThrow(() -> new ResourceNotFoundException());
	}

	public List<RepliesCount> getRepliesCountByPostIds(Iterable<Long> ids) {
		return postRepository.countRepliesByPostIdsIn(ids);
	}

	public Page<Long> getPostIdsByUserId(UUID userId, Pageable pageable) {
		return postRepository.findPostIdsByUserId(userId, pageable);
	}
}
