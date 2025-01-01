package com.gray.bird.user.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record UserAttributes(String username, String handle, String bio, LocalDate dateOfBirth,
	String location, String profileImage, LocalDateTime createdAt) {
}
