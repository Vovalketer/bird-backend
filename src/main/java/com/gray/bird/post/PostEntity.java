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

import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gray.bird.common.entity.TimestampedEntity;

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
	@Column(nullable = false)
	private UUID userId;
	private String text;
	private ReplyType replyType;
	private boolean deleted;
	// improves latency since we wouldnt need to query the media service if the post has no media
	private boolean hasMedia;
	@OneToMany(mappedBy = "parentPost")
	private Set<PostEntity> replies;
	@ManyToOne(fetch = FetchType.LAZY)
	private PostEntity parentPost;
	@Column(name = "parent_post_id", updatable = false, insertable = false)
	private Long parentPostId;
}
