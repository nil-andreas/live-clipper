package com.nilsson.sentiment.service;

import com.nilsson.sentiment.client.twitch.TwitchClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class TwitchClipService {
    @NonNull
    private final OAuth2AuthorizedClientService clientService;
    @NonNull
    private final TwitchClient twitchClient;

    public Mono<String> createClip(String userId, OAuth2AuthenticationToken auth) {
        String userAccessToken = getAccessToken(auth);
        var broadcasterId = twitchClient.getBroadcasterId(userId, userAccessToken);
        return twitchClient.createClip(broadcasterId, userAccessToken);
    }

    private String getAccessToken(OAuth2AuthenticationToken token) {
        var client = clientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());
        return client.getAccessToken().getTokenValue();
    }
}
