package com.gray.bird.post.dto;

import java.util.Collection;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gray.bird.user.view.UserView;

@Deprecated
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record PostResponse(Collection<PostData> posts, Collection<UserView> users) {
	public PostResponse(PostData post, UserView user) {
		this(Collections.singletonList(post), Collections.singletonList(user));
	}
}
// metrics (likes, post, replies counts)
// author_id
// created_at
// reply_settings
// references(quoted posts, @user, parent)
// postId
// media
