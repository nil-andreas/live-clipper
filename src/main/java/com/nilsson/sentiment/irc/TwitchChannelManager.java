package com.nilsson.sentiment.irc;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@AllArgsConstructor
public class TwitchChannelManager {
    @NonNull
    private final TwitchIrc twitchIrc;
    @NonNull
    private final OAuth2AuthorizedClientService clientService;
    private static final ConcurrentMap<String, Flux<String>> streams = new ConcurrentHashMap<>();

    public Flux<String> subscribe(String channel, OAuth2AuthenticationToken token) {
        if (streams.containsKey(channel)) {
            return streams.get(channel);
        }
        var client = clientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());
        var userName = getUserName(token);
        var oathToken = client.getAccessToken().getTokenValue();
        Flux<String> subscription = twitchIrc.streamFromChannel(channel, userName, oathToken);
        streams.put(channel, subscription);
        return subscription;
    }

    private String getUserName(OAuth2AuthenticationToken token) {
        token.getPrincipal();
        return token.getPrincipal().getAttribute("preferred_username");
    }
}
