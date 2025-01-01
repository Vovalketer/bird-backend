package com.gray.bird.repost;

import lombok.Data;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Data
@Embeddable
public class RepostId implements Serializable {
	private Long userId;
	private Long postId;
}
