package com.gray.bird.auth.jwt;

import java.util.Set;

import com.gray.bird.role.RoleType;

public record TokenData(String username, Set<String> audience,
	RoleType role // nullable
) {
}
