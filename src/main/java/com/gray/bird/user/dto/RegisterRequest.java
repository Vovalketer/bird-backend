package com.gray.bird.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * UserRequest
 */
public record RegisterRequest(@NotEmpty(message = "Username is required") @NotBlank(
								  message = "Username is required") String username,

	@Email(message = "A valid email is required") @NotEmpty(
		message = "Email is required") @NotBlank(message = "Email is required") String email,

	@Size(min = 6, message = "Password should be at least 6 characters long") @NotEmpty(
		message = "Password is required") @NotBlank(message = "Password is required")
	String password,
	@NotEmpty(message = "Handle is required") @NotBlank(
		message = "Handle is required") String handle) {
}
