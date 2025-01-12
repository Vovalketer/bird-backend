package com.gray.bird.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.gray.bird.role.RoleType;

@Data
@AllArgsConstructor
@Builder
public class UserDataDto {
	private Long id;
	private UUID uuid;
	private String handle;
	private String username;
	private LocalDate dateOfBirth;
	private String profileImage;
	private String bio;
	private String location;
	private String email;
	/*
	 * Security
	 */
	private RoleType role;
	private boolean accountNonLocked;
	private boolean accountNonExpired;
	private boolean credentialsNonExpired;
	private boolean enabled;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime lastLogin;
}
