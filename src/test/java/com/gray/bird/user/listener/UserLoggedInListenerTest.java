package com.gray.bird.user.listener;

import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.UUID;

import com.gray.bird.auth.event.UserLoggedInEvent;
import com.gray.bird.user.UserService;

@SpringJUnitConfig
public class UserLoggedInListenerTest {
	@Mock
	private UserService userService;
	@InjectMocks
	private UserLoggedInListener listener;

	@Test
	void testOnUserLogin() {
		UUID userId = UUID.randomUUID();
		LocalDateTime time = LocalDateTime.now();
		UserLoggedInEvent event = new UserLoggedInEvent(userId, time);
		Mockito.doNothing().when(userService).updateLastLogin(userId, time);

		listener.onUserLogin(event);

		Mockito.verify(userService).updateLastLogin(userId, time);
	}
}
