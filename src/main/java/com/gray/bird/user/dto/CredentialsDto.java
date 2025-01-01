package com.gray.bird.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public class CredentialsDto {
	private char[] password;

	public void clearPassword() {
		Arrays.fill(password, '*');
	}
}
