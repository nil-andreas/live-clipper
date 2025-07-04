package com.nilsson.sentiment.message.twitch;

import com.nilsson.sentiment.domain.Message;
import com.nilsson.sentiment.message.MessageParsingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@Component
public class TwitchOfflineChatMessageParsing implements MessageParsingStrategy {
    private static final Set<String> ROBOT_USERS = Set.of("nightbot", "moobot", "streamelements");

    @Override
    public Predicate<String> filter() {
        return chat -> {
            try {
                String[] parts = chat.split(",");
                String username = parts[1];
                String message = parts[3];
                return !ROBOT_USERS.contains(username) && !message.contains("They've subscribed for") && !message.contains("gifted a Tier") && !message.matches("\"\\d\"");
            } catch (Exception e) {
                log.debug("Error parsing chat message: {}", chat, e);
                return false;
            }
        };
    }

    @Override
    public Function<String, Message> toMessage() {
        return chat -> {
            String[] parts = chat.split(",");
            Integer time = Integer.parseInt(parts[0]);
            String message = parts[3];
            return Message.builder()
                    .timestamp(time)
                    .text(message)
                    .build();
        };
    }
}
