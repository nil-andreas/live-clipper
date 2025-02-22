package com.nilsson.sentiment.message.twitch;

import com.nilsson.sentiment.domain.Message;
import com.nilsson.sentiment.message.MessageParsingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * Example chat line:
 * 210:<USERNAME>!<USERNAME>@<USERNAME>.tmi.twitch.tv PRIVMSG #solorenektononly :fear with ult and ghost lol
 * where: chatmessage = `fear with ult and ghost lol`
 * username    <USERNAME>
 * channel     solorenektononly
 **/
@Component
@Slf4j
@Primary
public class TwitchChatMessageParsing implements MessageParsingStrategy {
    private static final Set<String> ROBOT_USERS = Set.of("nightbot", "moobot", "streamelements");

    @Override
    public Predicate<String> filter() {
        return chat -> {
            try {
                var username = extractUsername(chat);
                var message = extractMessage(chat);
                return !ROBOT_USERS.contains(username)
                        && !message.contains("They've subscribed for")
                        && !message.contains("gifted a Tier")
                        && !message.matches("\"\\d\"");
            } catch (Exception e) {
                log.debug("Error parsing chat message: {}", chat, e);
                return false;
            }
        };
    }


    @Override
    public Function<String, Message> toMessage() {
        return chat -> Message.builder()
                .timestamp(extractTime(chat))
                .text(extractMessage(chat))
                .build();
    }

    private static Integer extractTime(String chat) {
        int firstColonIndex = chat.indexOf(":");
        return Integer.parseInt(chat.substring(0, firstColonIndex));
    }

    private static String extractMessage(String chat) {
        var beginChatMessage = chat.substring(1).indexOf(':');
        return chat.substring(beginChatMessage);
    }

    private static String extractUsername(String chat) {
        var firstColonIndex = chat.indexOf(":");
        var exclamationPointIndex = chat.indexOf("!", firstColonIndex);
        return chat.substring(firstColonIndex, exclamationPointIndex);
    }
}
