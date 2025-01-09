package com.gray.bird.user.registration.listener;

import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.UUID;

import com.gray.bird.user.UserCommandService;
import com.gray.bird.user.command.EnableAccountCommand;
import com.gray.bird.user.mapper.EventToCommandMapper;
import com.gray.bird.user.registration.event.AccountVerifiedEvent;

@SpringJUnitConfig
public class AccountVerifiedListenerTest {
	@Mock
	private UserCommandService userCommandService;
	@Mock
	private EventToCommandMapper mapper;
	@InjectMocks
	private AccountVerifiedListener accountVerifiedListener;

	@Test
	void testOnAccountVerified() {
		AccountVerifiedEvent event = new AccountVerifiedEvent(UUID.randomUUID());
		EnableAccountCommand command = new EnableAccountCommand(event.userId());
		Mockito.when(mapper.toEnableAccountCommand(Mockito.any(AccountVerifiedEvent.class)))
			.thenReturn(command);

		Mockito.doNothing().when(userCommandService).enableAccount(Mockito.any(EnableAccountCommand.class));

		accountVerifiedListener.onAccountVerified(event);

		Mockito.verify(userCommandService, Mockito.times(1))
			.enableAccount(Mockito.any(EnableAccountCommand.class));
		Mockito.verify(mapper, Mockito.times(1))
			.toEnableAccountCommand(Mockito.any(AccountVerifiedEvent.class));
	}
}
