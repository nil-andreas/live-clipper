package com.nilsson.sentiment.client.twitch;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class TwitchClient {
    public static final String USERS_URL = "https://api.twitch.tv/helix/users/";
    public static final String CLIPS_URL = "https://api.twitch.tv/helix/clips";
    @Value("${spring.security.oauth2.client.registration.twitch.clientId}")
    private String twitchClientId;

    public Mono<String> getBroadcasterId(String userId, String userAccessToken) {
        var webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken)
                .defaultHeader("Client-Id", twitchClientId)
                .baseUrl(USERS_URL)
                .build();
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("login", userId).build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(root -> root.path("data").get(0).path("id").asText());
    }

    public Mono<String> createClip(Mono<String> broadcasterId, String userAccessToken) {
        var webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken)
                .defaultHeader("Client-Id", twitchClientId)
                .baseUrl(CLIPS_URL)
                .build();
        return broadcasterId.flatMap(id ->
                webClient.post()
                        .uri(uriBuilder -> uriBuilder.queryParam("broadcaster_id", id).build())
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .map(root -> root.path("data").get(0).path("edit_url").asText()));
    }

}
