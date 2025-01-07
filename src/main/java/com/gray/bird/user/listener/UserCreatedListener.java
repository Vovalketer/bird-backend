package com.gray.bird.user.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.gray.bird.email.EmailService;
import com.gray.bird.user.event.UserCreatedEvent;
import com.gray.bird.user.registration.AccountVerificationService;

@Component
@RequiredArgsConstructor
public class UserCreatedListener {
	private final EmailService emailService;
	private final AccountVerificationService accountVerificationService;

	@EventListener
	public void onUserCreated(UserCreatedEvent event) {
		String token = accountVerificationService.createVerificationToken(event.userId());
		emailService.sendNewAccountEmail(event.handle(), event.email(), token);
	}
}
