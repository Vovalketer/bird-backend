package com.gray.bird.user.event;

import com.gray.bird.user.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * UserEvent
 */
@Getter
@Setter
@AllArgsConstructor
public class UserEvent {
	private UserEntity user;
	private EventType type;
	private Map<?, ?> data;
}
