package com.gray.bird.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Builder
public class UserProfileDto {
	String username;
	String handle;
	LocalDate dateOfBirth;
	String profileImage;
	String bio;
	String location;
}
