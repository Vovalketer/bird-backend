package com.gray.bird.user.mapper;

import org.springframework.stereotype.Component;

import com.gray.bird.user.command.EnableAccountCommand;
import com.gray.bird.user.registration.event.AccountVerifiedEvent;

@Component
public class EventToCommandMapper {
	public EnableAccountCommand toEnableAccountCommand(AccountVerifiedEvent event) {
		return new EnableAccountCommand(event.userId());
	}
}
