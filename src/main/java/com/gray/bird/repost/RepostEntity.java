package com.gray.bird.repost;

import org.springframework.data.annotation.CreatedDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import com.gray.bird.common.entity.TimestampedEntity;
import com.gray.bird.post.PostEntity;
import com.gray.bird.user.UserEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reposts")
public class RepostEntity extends TimestampedEntity {
	@EmbeddedId
	private RepostId id;

	@MapsId("userId")
	@ManyToOne
	private UserEntity user;
	@Column(name = "user_id", updatable = false, insertable = false)
	private Long userId;

	@MapsId("postId")
	@ManyToOne
	private PostEntity post;
	@Column(name = "post_id", updatable = false, insertable = false)
	private Long postId;

	@CreatedDate
	private LocalDateTime repostedAt;

	public RepostEntity(UserEntity user, PostEntity post) {
		this.user = user;
		this.post = post;
	}
}
