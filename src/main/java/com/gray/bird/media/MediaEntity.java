package com.gray.bird.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
@ToString
@Entity
@Builder
@Table(name = "media", indexes = @Index(name = "idx_media_post", columnList = "post_id", unique = false))
public class MediaEntity extends TimestampedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long postId;
	private UUID userId;
	private int sortOrder;
	private String relativePath;
	private String filename;
	private String originalFilename;
	private String alt;
	private int width;
	private int height;
	private long fileSize;
	private Integer duration;
	private String mimeType;
}
