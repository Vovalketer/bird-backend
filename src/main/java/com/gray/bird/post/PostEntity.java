package com.gray.bird.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gray.bird.common.entity.TimestampedEntity;
import com.gray.bird.like.LikeEntity;
import com.gray.bird.media.MediaEntity;
import com.gray.bird.repost.RepostEntity;
import com.gray.bird.user.UserEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Table(name = "posts", indexes = @Index(name = "idx_posts_user", columnList = "user_id", unique = false))
public class PostEntity extends TimestampedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private UserEntity user;
	@Column(name = "user_id", nullable = false, updatable = false, insertable = false)
	private Long userId;
	// decided on denormalization in case the client decides not to include the user in the request
	// so we dont have to join/query to fetch the userReferenceId to present it in the response
	@Column(nullable = false, updatable = false, insertable = false)
	private String userReferenceId;
	private String text;
	private ReplyType replyType;
	private boolean deleted;
	@OneToMany(mappedBy = "post")
	private Set<MediaEntity> media;
	@OneToMany(mappedBy = "post")
	private Set<LikeEntity> likes;
	@OneToMany(mappedBy = "post")
	private Set<RepostEntity> reposts;
	@OneToMany(mappedBy = "parentPost")
	private Set<PostEntity> replies;
	@ManyToOne(fetch = FetchType.LAZY)
	private PostEntity parentPost;
	@Column(name = "parent_post_id", updatable = false, insertable = false)
	private Long parentPostId;

	public Set<PostEntity> getReplies() {
		if (replies == null) {
			replies = new HashSet<>();
		}
		return replies;
	}

	public Set<MediaEntity> getMedia() {
		if (media == null) {
			media = new HashSet<>();
		}
		return media;
	}

	public Set<LikeEntity> getLikes() {
		if (likes == null) {
			likes = new HashSet<>();
		}
		return likes;
	}

	public Set<RepostEntity> getReposts() {
		if (reposts == null) {
			reposts = new HashSet<>();
		}
		return reposts;
	}

	@Override
	public String toString() {
		return "PostEntity [id=" + id + ", user=" + user + ", userId=" + userId
			+ ", userReferenceId=" + userReferenceId + ", text=" + text + ", replyType=" + replyType
			+ ", deleted=" + deleted + ", parentPostId=" + parentPostId + "]";
	}
}
