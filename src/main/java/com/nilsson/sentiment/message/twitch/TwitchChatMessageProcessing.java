package com.nilsson.sentiment.message.twitch;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

import com.nilsson.sentiment.message.MessageProcessingStrategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TwitchChatMessageProcessing implements MessageProcessingStrategy {
	private static final Set<String> ROBOT_USERS = Set.of("nightbot", "moobot", "streamelements");

	@Override
	public Predicate<String> filter() {
		return chat -> {
			try {
				String[] parts = chat.split(",");
				String username = parts[1];
				String message = parts[3];
				return !ROBOT_USERS.contains(username) && !message.contains("They've subscribed for");
			} catch (Exception e) {
				log.debug("Error parsing chat message: {}", chat, e);
				return false;
			}
		};
	}

	@Override
	public Function<String, Map.Entry<Integer, String>> toMessage() {
		return chat -> {
			String[] parts = chat.split(",");
			Integer time = Integer.parseInt(parts[0]);
			String message = parts[3];
			return Map.entry(time, message);
		};
	}
}
