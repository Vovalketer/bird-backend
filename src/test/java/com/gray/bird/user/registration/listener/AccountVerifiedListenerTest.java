package com.gray.bird.user.registration.listener;

import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.UUID;

import com.gray.bird.user.UserService;
import com.gray.bird.user.registration.event.AccountVerifiedEvent;

@SpringJUnitConfig
public class AccountVerifiedListenerTest {
	@Mock
	private UserService userService;
	@InjectMocks
	private AccountVerifiedListener accountVerifiedListener;

	@Test
	void testOnAccountVerified() {
		AccountVerifiedEvent event = new AccountVerifiedEvent(UUID.randomUUID());

		Mockito.doNothing().when(userService).enableAccount(Mockito.any(UUID.class));

		accountVerifiedListener.onAccountVerified(event);

		Mockito.verify(userService, Mockito.times(1)).enableAccount(Mockito.any(UUID.class));
	}
}
