package com.gray.bird.auth;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.gray.bird.exception.ApiException;
import com.gray.bird.user.CredentialsEntity;
import com.gray.bird.user.CredentialsRepository;
import com.gray.bird.user.UserMapper;

@Service
@RequiredArgsConstructor
public class UserPrincipalService implements UserDetailsService {
	private final CredentialsRepository credentialsRepository;
	private final UserMapper userMapper;

	@Override
	public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
		// avoid service dependencies
		CredentialsEntity credentials =
			credentialsRepository.findCredentialsByUserUsernameIgnoreCase(username).orElseThrow(
				() -> new ApiException("Unable to find the credentials"));
		return new UserPrincipal(
			userMapper.toUserDto(credentials.getUser()), userMapper.toCredentialsDto(credentials));
	}

	public UserPrincipal loadUserByEmail(String email) {
		CredentialsEntity credentials =
			credentialsRepository.findCredentialsByUserEmailIgnoreCase(email).orElseThrow(
				() -> new ApiException("Unable to find the credentials"));
		return new UserPrincipal(
			userMapper.toUserDto(credentials.getUser()), userMapper.toCredentialsDto(credentials));
	}

	public UserPrincipal loadUserByReferenceId(String referenceId) {
		CredentialsEntity credentials =
			credentialsRepository.findCredentialsByUserReferenceId(referenceId)
				.orElseThrow(() -> new ApiException("Unable to find the credentials"));
		return new UserPrincipal(
			userMapper.toUserDto(credentials.getUser()), userMapper.toCredentialsDto(credentials));
	}
}
