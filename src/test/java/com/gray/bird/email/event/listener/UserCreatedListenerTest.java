package com.gray.bird.email.event.listener;

import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.gray.bird.email.IEmailService;
import com.gray.bird.user.event.UserCreatedEvent;

@SpringJUnitConfig
public class UserCreatedListenerTest {
	@Mock
	private IEmailService emailService;
	@InjectMocks
	private UserCreatedListener userCreatedListener;

	@Test
	void testOnUserCreated() {
		String handle = "handle";
		String email = "email@test.com";
		String token = "token";
		UserCreatedEvent event = new UserCreatedEvent(handle, email, token);

		Mockito.doNothing().when(emailService).sendNewAccountEmail(handle, email, token);

		userCreatedListener.onUserCreated(event);

		Mockito.verify(emailService).sendNewAccountEmail(handle, email, token);
	}
}
