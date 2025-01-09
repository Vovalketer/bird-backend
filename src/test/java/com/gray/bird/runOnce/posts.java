package com.gray.bird.runOnce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import com.gray.bird.post.PostEntity;
import com.gray.bird.post.PostRepository;
import com.gray.bird.user.UserEntity;
import com.gray.bird.user.UserRepository;
import com.gray.bird.utils.TestUtils;
import com.gray.bird.utils.TestUtilsFactory;

@SpringBootTest
public class posts {
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private UserRepository userRepository;

	private TestUtils testUtils = TestUtilsFactory.createTestUtils();

	@Test
	void addPosts() {
		Optional<UserEntity> user = userRepository.findById(1L);
		PostEntity post = testUtils.createPost();
		post.setParentPost(null);
		post.setParentPostId(null);
		post.setText("testPost1");
		post.setUserId(user.get().getUuid());

		postRepository.save(post);
	}
}
