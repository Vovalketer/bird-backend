package com.gray.bird.auth.event;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class AuthEvent extends ApplicationEvent {
	String username;
	EventType type;
	LocalDateTime dateTime;

	public AuthEvent(Object source, String referenceId, EventType type, LocalDateTime dateTime) {
		super(source);
		this.username = referenceId;
		this.type = type;
		this.dateTime = dateTime;
	}
}
