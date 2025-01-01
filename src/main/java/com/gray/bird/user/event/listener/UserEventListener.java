package com.gray.bird.user.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.gray.bird.email.EmailService;
import com.gray.bird.user.event.UserEvent;

/**
 * UserEventListener
 */
@Component
@RequiredArgsConstructor
public class UserEventListener {
	private final EmailService emailService;

	@EventListener
	public void onUserEvent(UserEvent event) {
		switch (event.getType()) {
			case REGISTRATION ->
				emailService.sendNewAccountEmail(event.getUser().getHandle(),
					event.getUser().getEmail(),
					(String) event.getData().get("token"));
			case RESETPASSWORD ->
				emailService.sendPasswordResetEmail(event.getUser().getHandle(),
					event.getUser().getEmail(),
					(String) event.getData().get("token"));
		}
	}
}
