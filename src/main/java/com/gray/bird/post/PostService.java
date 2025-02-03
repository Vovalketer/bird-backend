package com.gray.bird.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import com.gray.bird.exception.InvalidPostException;
import com.gray.bird.exception.ResourceNotFoundException;
import com.gray.bird.media.MediaCommandService;
import com.gray.bird.post.dto.PostCreationRequest;
import com.gray.bird.post.dto.PostProjection;
import com.gray.bird.post.dto.RepliesCount;
import com.gray.bird.post.event.PostEventPublisher;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class, readOnly = true)
public class PostService {
	private final PostRepository postRepository;
	private final MediaCommandService mediaService;
	private final PostMapper postMapper;
	private final PostEventPublisher postEventPublisher;

	@Transactional
	public PostEntity savePost(PostEntity post) {
		return postRepository.save(post);
	}

	public PostEntity getByPostId(Long postId) {
		return postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException());
	}

	@Transactional
	public PostProjection createPost(PostCreationRequest postRequest, UUID userId) {
		if (isPostEmpty(postRequest)) {
			throw new InvalidPostException();
		}
		boolean hasMedia = hasMedia(postRequest);
		if (hasMedia) {
			mediaService.uploadImages(postRequest.media());
		}
		PostEntity post = createPostEntity(postRequest, userId, hasMedia);
		PostEntity savedPost = savePost(post);

		postEventPublisher.publishPostCreatedEvent(savedPost.getUserId(), savedPost.getId());
		return postMapper.toPostProjection(savedPost);
	}

	@Transactional
	public PostProjection createReply(PostCreationRequest postRequest, Long parentPostId, UUID userId) {
		if (isPostEmpty(postRequest)) {
			throw new InvalidPostException();
		}
		PostEntity parent = getByPostId(parentPostId);
		boolean hasMedia = hasMedia(postRequest);
		if (hasMedia) {
			mediaService.uploadImages(postRequest.media());
		}
		PostEntity post = createPostEntity(postRequest, parent, userId, hasMedia);

		PostEntity savedPost = savePost(post);

		return postMapper.toPostProjection(savedPost);
	}

	private PostEntity createPostEntity(PostCreationRequest post, UUID userId, boolean hasMedia) {
		return PostEntity.builder()
			.text(post.text())
			.replyType(post.replyType())
			.userId(userId)
			.hasMedia(hasMedia)
			.build();
	}

	private PostEntity createPostEntity(
		PostCreationRequest post, PostEntity parent, UUID userId, boolean hasMedia) {
		return PostEntity.builder()
			.text(post.text())
			.replyType(post.replyType())
			.userId(userId)
			.parentPost(parent)
			.hasMedia(hasMedia)
			.build();
	}

	private boolean hasMedia(PostCreationRequest postRequest) {
		return postRequest.media() != null;
	}

	private boolean hasText(PostCreationRequest postRequest) {
		return postRequest.text() != null && !postRequest.text().isBlank();
	}

	private boolean isPostEmpty(PostCreationRequest postRequest) {
		return !hasText(postRequest) && !hasMedia(postRequest);
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
		return postRepository.countRepliesByPostId(id).orElse(new RepliesCount(id, 0L));
	}

	public List<RepliesCount> getRepliesCountByPostIds(Iterable<Long> ids) {
		return postRepository.countRepliesByPostIdsIn(ids);
	}

	public Page<Long> getPostIdsByUserId(UUID userId, Pageable pageable) {
		return postRepository.findPostIdsByUserId(userId, pageable);
	}
}
