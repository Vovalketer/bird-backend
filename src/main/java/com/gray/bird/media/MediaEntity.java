package com.gray.bird.media;

import org.springframework.http.MediaType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.UUID;

import com.gray.bird.common.entity.TimestampedEntity;

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
	private Long postId;
	private UUID userId;
	private String url;
	private String alt;
	private int width;
	private int height;
	private long size;
	private Integer duration;
	private MediaType type;
}
