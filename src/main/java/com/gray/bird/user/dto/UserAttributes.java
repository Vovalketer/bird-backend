package com.gray.bird.user.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("users")
public record UserAttributes(String username, String handle, String bio, LocalDate dateOfBirth,
	String location, String profileImage, LocalDateTime createdAt) {
}
