package com.nilsson.sentiment.service;

import com.nilsson.sentiment.PlotInput;
import com.nilsson.sentiment.domain.Message;
import com.nilsson.sentiment.irc.TwitchChannelManager;
import com.nilsson.sentiment.message.MessageParser;
import com.nilsson.sentiment.score.MessageScorer;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@AllArgsConstructor
@Service
public class TwitchChatService {
    @NonNull
    private final TwitchChannelManager twitchChannelManager;
    @NonNull
    private final MessageParser messageParser;
    @NonNull
    private final MessageScorer messageScorer;

    public Flux<PlotInput> streamFromChannel(String channel, OAuth2AuthenticationToken token) {
        Flux<String> chatStream = twitchChannelManager.subscribe(channel, token);
        Flux<Message> parsedMessages = messageParser.parse(chatStream);
        return messageScorer.score(parsedMessages);
    }
}
