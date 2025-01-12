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
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.gray.bird.common.entity.TimestampedEntity;
import com.gray.bird.role.RoleEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users",
	indexes =
	{
		@Index(name = "idx_users_uuid", columnList = "uuid", unique = true)
		, @Index(name = "idx_users_username", columnList = "username", unique = true),
			@Index(name = "idx_users_email", columnList = "email", unique = true)
	})
public class UserEntity extends TimestampedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private Long id;
	@Column(unique = true, nullable = false)
	private UUID uuid;
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
}
