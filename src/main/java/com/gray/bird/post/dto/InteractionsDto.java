package com.gray.bird.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@AllArgsConstructor
public class InteractionsDto {
	@JsonIgnore
	Long postId;
	long repliesCount;
	long likesCount;
	long repostsCount;
}
