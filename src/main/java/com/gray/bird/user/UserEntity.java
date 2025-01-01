package com.gray.bird.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.gray.bird.common.entity.TimestampedEntity;
import com.gray.bird.like.LikeEntity;
import com.gray.bird.post.PostEntity;
import com.gray.bird.repost.RepostEntity;
import com.gray.bird.role.RoleEntity;
import com.gray.bird.user.follow.FollowEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users",
	indexes =
	{
		@Index(name = "idx_users_reference", columnList = "referenceId", unique = true)
		, @Index(name = "idx_users_username", columnList = "username", unique = true),
			@Index(name = "idx_users_email", columnList = "email", unique = true)
	})
public class UserEntity extends TimestampedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private Long id;
	@Column(unique = true, nullable = false)
	private String referenceId;
	@Column(nullable = false)
	private String handle;
	@Column(unique = true, nullable = false)
	private String username;
	private LocalDate dateOfBirth;
	private String profileImage;
	private String bio;
	private String location;
	@Column(unique = true, nullable = false)
	private String email;
	@OneToMany(mappedBy = "user")
	private Set<PostEntity> posts;
	@OneToMany(mappedBy = "user")
	private Set<RepostEntity> reposts;
	@OneToMany(mappedBy = "user")
	@Builder.Default
	private Set<LikeEntity> likes = new HashSet<>();

	@OneToMany(mappedBy = "followingUser")
	private Set<FollowEntity> following;
	@OneToMany(mappedBy = "followedUser")
	private Set<FollowEntity> followers;
	/*
	 * Security
	 */
	@ManyToOne
	private RoleEntity role;
	private boolean accountNonLocked;
	private boolean accountNonExpired;
	private boolean credentialsNonExpired;
	private boolean enabled;
	private LocalDateTime lastLogin;

	@Override
	public String toString() {
		return "UserEntity [id=" + id + ", referenceId=" + referenceId + ", handle=" + handle + ", username="
			+ username + ", dateOfBirth=" + dateOfBirth + ", profileImage=" + profileImage + ", bio=" + bio
			+ ", location=" + location + ", email=" + email + ", lastLogin=" + lastLogin + "]";
	}
}
