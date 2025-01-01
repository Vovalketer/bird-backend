package com.gray.bird.post.view;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Immutable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gray.bird.post.ReplyType;

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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "posts_view")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class PostView {
	@Id
	private Long id;
	@JsonIgnore
	private Long userId;
	private String userReferenceId;
	private String text;
	private boolean deleted;
	private ReplyType replyType;
	private Long parentPostId;
	private LocalDateTime createdAt;
}
