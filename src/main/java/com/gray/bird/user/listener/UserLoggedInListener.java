package com.gray.bird.user.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.gray.bird.auth.event.UserLoggedInEvent;
import com.gray.bird.user.UserService;

@Component
@RequiredArgsConstructor
public class UserLoggedInListener {
	private final UserService userService;

	@EventListener
	public void onUserLogin(UserLoggedInEvent event) {
		userService.updateLastLogin(event.userId(), event.time());
	}
}
