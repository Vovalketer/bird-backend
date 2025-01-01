package com.gray.bird.auth.event;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.gray.bird.user.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthEventListener implements ApplicationListener<AuthEvent> {
	private final UserService userService;

	@Override
	public void onApplicationEvent(AuthEvent event) {
		switch (event.getType()) {
		case LOGIN -> {
			userService.updateLastLogin(event.getUsername(), event.getDateTime());
		}
		}
	}
}
