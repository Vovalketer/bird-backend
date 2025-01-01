package com.gray.bird.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

import com.gray.bird.exception.ApiException;
import com.gray.bird.user.dto.UserDataDto;

public class UserAuthentication extends AbstractAuthenticationToken {
	private static final String PASSWORD_PROTECTED = "[PASSWORD_PROTECTED]";
	private static final String EMAIL_PROTECTED = "[EMAIL_PROTECTED]";
	private UserDataDto user;
	private String email;
	private String password;
	private boolean authenticated;

	private UserAuthentication(String email, String password) {
		super(AuthorityUtils.NO_AUTHORITIES);
		this.email = email;
		this.password = password;
		this.authenticated = false;
	}

	private UserAuthentication(
		UserDataDto user, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.user = user;
		this.email = EMAIL_PROTECTED;
		this.password = PASSWORD_PROTECTED;
		this.authenticated = true;
	}

	public static UserAuthentication unauthenticated(String email, String password) {
		return new UserAuthentication(email, password);
	}

	public static UserAuthentication authenticated(
		UserDataDto user, Collection<? extends GrantedAuthority> authorities) {
		return new UserAuthentication(user, authorities);
	}

	@Override
	public Object getCredentials() {
		return PASSWORD_PROTECTED;
	}

	@Override
	public Object getPrincipal() {
		return this.user;
	}
	@Override
	public void setAuthenticated(boolean authenticated) {
		throw new ApiException("This acction is now allowed");
	}

	@Override
	public boolean isAuthenticated() {
		return this.authenticated;
	}

	public String getEmail() {
		return this.email;
	}

	public CharSequence getPassword() {
		return this.password;
	}
}
