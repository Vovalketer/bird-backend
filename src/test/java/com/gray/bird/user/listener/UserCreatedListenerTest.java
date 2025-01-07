package com.gray.bird.user.listener;

import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.gray.bird.email.EmailService;
import com.gray.bird.user.event.UserCreatedEvent;
import com.gray.bird.user.registration.AccountVerificationService;

@SpringJUnitConfig
public class UserCreatedListenerTest {
	@Mock
	private EmailService emailService;
	@Mock
	private AccountVerificationService accountVerificationService;
	@InjectMocks
	private UserCreatedListener userCreatedListener;

	@Test
	void testOnUserCreated() {
		UserCreatedEvent event = new UserCreatedEvent(1L, "referenceId", "handle", "email");
		Mockito.when(accountVerificationService.createVerificationToken(Mockito.anyLong()))
			.thenReturn("token");
		Mockito.doNothing()
			.when(emailService)
			.sendNewAccountEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

		userCreatedListener.onUserCreated(event);

		Mockito.verify(emailService, Mockito.times(1))
			.sendNewAccountEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.verify(accountVerificationService, Mockito.times(1))
			.createVerificationToken(Mockito.anyLong());
	}
}
