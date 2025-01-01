package com.gray.bird.user.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gray.bird.role.RoleType;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Builder
public record UserProjection(@JsonIgnore Long id, String referenceId, String username, String handle,
	String bio, LocalDate dateOfBirth, String location, RoleType roleType, String profileImage,
	LocalDateTime createdAt) {
}
