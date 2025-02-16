package com.nilsson.sentiment.endpoint.api;

import com.nilsson.sentiment.service.TwitchClipService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class TwitchClipController {
    @NonNull
    private final TwitchClipService twitchClipService;

    @GetMapping("/clip/{userId}")
    public ResponseEntity<Mono<String>> createClip(@PathVariable String userId, OAuth2AuthenticationToken token) {
        log.debug("createClip for user {}", userId);
        return ResponseEntity.ok(twitchClipService.createClip(userId, token));
    }
}
