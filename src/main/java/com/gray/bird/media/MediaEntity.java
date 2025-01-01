package com.gray.bird.media;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.gray.bird.common.entity.TimestampedEntity;
import com.gray.bird.post.PostEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "media", indexes = @Index(name = "idx_media_post", columnList = "post_id", unique = false))
public class MediaEntity extends TimestampedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private PostEntity post;
	private String url;
	private String description;
	private Integer width;
	private Integer height;
	private long fileSize;
	private Long duration;
	private String format;
}
