package com.gray.bird.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.gray.bird.role.RoleType;

@Data
@AllArgsConstructor
public class UserDataDto {
	private Long id;
	private String referenceId;
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
