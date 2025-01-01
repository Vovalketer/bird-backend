package com.gray.bird.like;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import com.gray.bird.post.PostEntity;
import com.gray.bird.user.UserEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "likes")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class LikeEntity {
	@EmbeddedId
	private LikeId id = new LikeId();

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "user_id")
	@MapsId("userId")
	private UserEntity user;
	@Column(name = "user_id", updatable = false, insertable = false)
	private Long userId;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "post_id")
	@MapsId("postId")
	private PostEntity post;
	@Column(name = "post_id", updatable = false, insertable = false)
	private Long postId;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdDate;

	public LikeEntity(UserEntity user, PostEntity post) {
		this.user = user;
		this.post = post;
	}
}
