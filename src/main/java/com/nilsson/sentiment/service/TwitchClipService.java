package com.nilsson.sentiment.service;

import com.nilsson.sentiment.ClipRepository;
import com.nilsson.sentiment.client.twitch.TwitchClient;
import com.nilsson.sentiment.domain.AuthorizedClientKey;
import com.nilsson.sentiment.domain.Clip;
import com.nilsson.sentiment.entity.ClipEntity;
import com.nilsson.sentiment.mapper.ClipMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class TwitchClipService {
    @NonNull
    private final OAuth2AuthorizedClientService clientService;
    @NonNull
    private final TwitchClient twitchClient;
    @NonNull
    private final ClipMapper clipMapper;
    @NonNull
    private final ClipRepository clipRepository;

    public Mono<Clip> createClip(String userId, OAuth2AuthenticationToken auth) {
        String userAccessToken = getAccessToken(auth);
        return createClip(userId, userAccessToken);
    }

    public Mono<Clip> createClip(String userId, AuthorizedClientKey key) {
        String userAccessToken = getAccessToken(key);
        return createClip(userId, userAccessToken);
    }

    public void storeClip(Clip clip) {
        ClipEntity entity = clipMapper.toEntity(clip);
        clipRepository.save(entity);

    }

    private Mono<Clip> createClip(String userId, String userAccessToken) {
        var broadcasterId = twitchClient.getBroadcasterId(userId, userAccessToken);
        return twitchClient.createClip(broadcasterId, userAccessToken)
                .map(url -> Clip.builder()
                        .url(url)
                        .timestamp(LocalDateTime.now())
                        .channel(userId)
                        .build());
    }

    private String getAccessToken(AuthorizedClientKey key) {
        var client = clientService.loadAuthorizedClient(key.getRegistrationId(), key.getPrincipalName());
        return client.getAccessToken().getTokenValue();
    }

    private String getAccessToken(OAuth2AuthenticationToken token) {
        var client = clientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());
        return client.getAccessToken().getTokenValue();
    }
}
