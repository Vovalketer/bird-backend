package com.gray.bird.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gray.bird.auth.AuthService;
import com.gray.bird.exception.GlobalExceptionHandler;
import com.gray.bird.media.MediaCommandService;
import com.gray.bird.media.view.MediaViewService;
import com.gray.bird.post.view.PostViewService;
import com.gray.bird.postAggregate.PostAggregateQueryService;
import com.gray.bird.user.UserEntity;
import com.gray.bird.user.UserService;
import com.gray.bird.user.view.UserViewService;
import com.gray.bird.utils.TestUtils;

@Import(GlobalExceptionHandler.class)
@ExtendWith(MockitoExtension.class)
public class PostAggregatorServiceTest {
	@Mock
	private PostCommandService postService;
	@Mock
	private UserService userService;
	@Mock
	private MediaCommandService mediaService;
	@Mock
	private InteractionsService interactionsService;
	@Mock
	private PostViewService postViewService;
	@Mock
	private UserViewService userViewService;
	@Mock
	private MediaViewService mediaViewService;

	@Mock
	private AuthService authService;

	@InjectMocks
	private PostAggregateQueryService postAggregateService;

	@Autowired
	private TestUtils testUtils;

	private final UserEntity user = testUtils.createUser();
	private final PostEntity post = testUtils.createPost();
	private final Pageable pageable = PageRequest.of(0, 10);

	// @Test
	// void testCreatePost() {
	// PostRequest request = new PostRequest("test_text", null, ReplyType.EVERYONE);
	// Mockito.when(authService.getPrincipalUsername()).thenReturn(user.getUsername());
	// Mockito.when(userService.getUserEntityByUsername(Mockito.anyString())).thenReturn(user);
	// Mockito.when(mediaService.uploadImages(Mockito.any())).thenReturn(post.getMedia());
	// Mockito.when(postService.savePost(Mockito.any(PostEntity.class))).thenReturn(post);
	//
	// PostDto response = taService.createPost(request);
	//
	// Assertions.assertThat(response).isNotNull();
	// Assertions.assertThat(response.getId()).isEqualTo(post.getId());
	// Assertions.assertThat(response.getUserReferenceId()).isEqualTo(post.getUser().getReferenceId());
	// }
	//
	// @Test
	// void testCreateReply() {
	// PostRequest request = new PostRequest("test_text", null, ReplyType.EVERYONE);
	// Mockito.when(authService.getPrincipalUsername()).thenReturn(user.getUsername());
	// Mockito.when(userService.getUserEntityByUsername(Mockito.anyString())).thenReturn(user);
	// Mockito.when(postService.getByPostId(Mockito.anyLong())).thenReturn(post.getParentPost());
	// Mockito.when(mediaService.uploadImages(Mockito.any())).thenReturn(post.getMedia());
	// Mockito.when(postService.savePost(Mockito.any(PostEntity.class))).thenReturn(post);
	//
	// PostDto response = taService.createReply(request, post.getId());
	//
	// Assertions.assertThat(response).isNotNull();
	// Assertions.assertThat(response.getId()).isEqualTo(post.getId());
	// Assertions.assertThat(response.getUserReferenceId()).isEqualTo(post.getUser().getReferenceId());
	// }
	//
	// @Test
	// void testGetReplies() {
	// Page<PostView> postViewPage = testUtils.createPostViewPage(pageable);
	// List<Long> ids = postViewPage.stream().map(m -> m.getId()).collect(Collectors.toList());
	// List<MediaView> mediaViews = testUtils.createMediaViews(ids);
	// List<InteractionsView> interactionViews = testUtils.createInteractionsView(ids);
	// when(postViewService.getRepliesByPostId(anyLong(),
	// Mockito.any(Pageable.class))).thenReturn(postViewPage);
	// when(mediaViewService.getAllPostMediaById(ids)).thenReturn(mediaViews);
	// when(interactionsViewService.getAllInteractionsById(ids)).thenReturn(interactionViews);
	//
	// PageResponse<PostResponse> replies = taService.getReplies(1L, pageable.getPageNumber(),
	// pageable.getPageSize());
	//
	// Assertions.assertThat(replies).isNotNull();
	// Assertions.assertThat(replies.getMeta().getNumberOfElements()).isGreaterThan(0);
	// Assertions.assertThat(replies.getData().posts().size()).isGreaterThan(0);
	// replies.getData().posts().stream().forEach(reply -> {
	// Assertions.assertThat(reply.id()).isIn(ids);
	// });
	// }
	//
	// @Test
	// void testGetPost() {
	// PostView postView = testUtils.createPostView();
	// UserView userView = testUtils.createUserView();
	// List<MediaView> mediaViews =
	// testUtils.createMediaViews(Collections.singletonList(postView.getId()));
	//
	// InteractionsView interactionsView = testUtils.createInteractionsView(postView.getId());
	// when(postViewService.getByPostId(anyLong())).thenReturn(postView);
	// when(userViewService.getUserById(anyLong())).thenReturn(userView);
	// when(mediaViewService.getPostMedia(anyLong())).thenReturn(mediaViews);
	// when(interactionsViewService.getInteractionsById(anyLong())).thenReturn(interactionsView);
	//
	// PostResponse postResponse = taService.getPost(postView.getId());
	//
	// Assertions.assertThat(postResponse).isNotNull();
	// Assertions.assertThat(postResponse.posts()).isNotNull();
	// Assertions.assertThat(postResponse.posts()).isNotEmpty();
	//
	// PostData post = postResponse.posts().stream().findFirst().orElseThrow();
	// Assertions.assertThat(post.id()).isEqualTo(postView.getId());
	// Assertions.assertThat(post.media()).isEqualTo(mediaViews);
	// Assertions.assertThat(post.interactions()).isEqualTo(interactionsView);
	//
	// Assertions.assertThat(postResponse.users()).isNotNull();
	// Assertions.assertThat(postResponse.users()).isNotEmpty();
	// UserView user = postResponse.users().stream().findFirst().orElseThrow();
	// Assertions.assertThat(user).isEqualTo(userView);
	// }
	//
	// @Test
	// void testGetPostsByUsername() {
	// Page<PostView> postViewPage = testUtils.createPostViewPage(pageable);
	// when(userViewService.getUserByUsername(anyString())).thenReturn(testUtils.createUserView());
	// when(postViewService.getByUserId(anyLong(),
	// Mockito.any(Pageable.class))).thenReturn(postViewPage);
	//
	// PageResponse<PostResponse> postsByUsername = taService.getPostsByUsername("testUsername",
	// pageable.getPageNumber(), pageable.getPageSize());
	//
	// Assertions.assertThat(postsByUsername).isNotNull();
	// Assertions.assertThat(postsByUsername.getData()).isNotNull();
	// Assertions.assertThat(postsByUsername.getMeta()).isNotNull();
	// }
	//
	// @Test
	// void testGetUserTimeline() {
	// }
	//
	// @Test
	// void testLikePost() {
	// when(authService.getPrincipalUsername()).thenReturn(user.getUsername());
	// when(userService.getUserEntityByUsername(anyString())).thenReturn(user);
	// Mockito.doNothing().when(interactionsService).likePost(Mockito.any(UserEntity.class),
	// Mockito.any(PostEntity.class));
	//
	// InteractionsView interactions = taService.likePost(1L);
	//
	// Assertions.assertThat(interactions).isNotNull();
	// }
	//
	// @Test
	// void testRepost() {
	// when(authService.getPrincipalUsername()).thenReturn(user.getUsername());
	// when(userService.getUserEntityByUsername(anyString())).thenReturn(user);
	// Mockito.doNothing().when(interactionsService).repost(Mockito.any(UserEntity.class),
	// Mockito.any(PostEntity.class));
	//
	// InteractionsView interactions = taService.repost(1L);
	//
	// Assertions.assertThat(interactions).isNotNull();
	// }
}
