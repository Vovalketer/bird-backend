package com.gray.bird.role;

import org.springframework.security.core.GrantedAuthority;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum RoleType implements GrantedAuthority {
	NULL,
	USER,
	MODERATOR,
	ADMIN,
	SUPER_ADMIN;

	@Override
	public String getAuthority() {
		return this.name();
	}

	public static RoleType getType(String role) {
		for (RoleType var : RoleType.values()) {
			if (var.name().equalsIgnoreCase(role)) {
				return var;
			}
		}
		return RoleType.NULL;
	}
}
