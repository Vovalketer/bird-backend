package com.gray.bird.media.view;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Immutable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "media_view")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class MediaView {
	@Id
	private Long id;
	private Long postId;
	private String url;
	private String description;
	private int width;
	private int height;
	private long fileSize;
	private long duration;
	private String format;
}
