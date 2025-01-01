package com.gray.bird.role;

public class RoleConstants {
	// this has some currently unused authorities
	public static final String AUTHORITIES = "authorities";
	public static final String ROLE = "role";
	public static final String ROLE_PREFIX = "ROLE_";
	public static final String AUTHORITY_DELIMITER = ",";
	public static final String USER_AUTHORITIES = "post:create, post:delete_own";
	public static final String MODERATOR_AUTHORITIES = "post:delete_all, user:disable";
	public static final String ADMIN_AUTHORITIES =
		"post:create, post:delete_all, user:update_role, user:delete, user:update";
	public static final String SUPER_ADMIN_AUTHORITIES =
		"post:create, post:delete_all, user:update_role, user:delete, user:update";
}
