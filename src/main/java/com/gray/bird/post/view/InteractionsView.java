package com.gray.bird.post.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Immutable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "interactions_view")
public class InteractionsView {
	@Id
	@Column(name = "post_id")
	@JsonIgnore
	private Long postId;
	private long replyCount;
	private long likesCount;
	private long repostsCount;
}
