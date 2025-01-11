package com.gray.bird.user.registration.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.gray.bird.user.UserService;
import com.gray.bird.user.registration.event.AccountVerifiedEvent;

@Component
@RequiredArgsConstructor
public class AccountVerifiedListener {
	private final UserService userService;

	@EventListener
	public void onAccountVerified(AccountVerifiedEvent event) {
		userService.enableAccount(event.userId());
	}
}
