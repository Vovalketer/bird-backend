package com.gray.bird.email.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.gray.bird.email.IEmailService;
import com.gray.bird.user.event.UserCreatedEvent;

@Component
@RequiredArgsConstructor
public class UserCreatedListener {
	private final IEmailService emailService;

	@EventListener
	public void onUserCreated(UserCreatedEvent event) {
		emailService.sendNewAccountEmail(event.handle(), event.email(), event.token());
	}
}
