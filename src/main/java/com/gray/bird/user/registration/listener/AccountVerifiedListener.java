package com.gray.bird.user.registration.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.gray.bird.user.UserCommandService;
import com.gray.bird.user.mapper.EventToCommandMapper;
import com.gray.bird.user.registration.event.AccountVerifiedEvent;

@Component
@RequiredArgsConstructor
public class AccountVerifiedListener {
	private final UserCommandService userCommandService;
	private final EventToCommandMapper eventToCommandMapper;

	@EventListener
	public void onAccountVerified(AccountVerifiedEvent event) {
		userCommandService.enableAccount(eventToCommandMapper.toEnableAccountCommand(event));
	}
}
