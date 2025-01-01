package com.gray.bird.auth;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.gray.bird.user.dto.CredentialsDto;
import com.gray.bird.user.dto.UserDataDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class UserPrincipal implements UserDetails {
	private final UserDataDto user;
	private final CredentialsDto credentials;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(user.getRole());
	}

	@Override
	public String getPassword() {
		// single use password

		String pw = String.valueOf(credentials.getPassword());
		log.info("USER HASH: {}", credentials.getPassword());
		log.info("(VAR PW)USER HASH: {}", pw);
		credentials.clearPassword();
		log.info("(VAR PW)USER HASH AFTER CLEAR: {}", pw);
		credentials.getPassword();
		log.info("USER HASH AFTER CLEAR: {}", credentials.getPassword());
		return pw;
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return user.isAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return user.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return user.isCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return user.isEnabled();
	}
}
