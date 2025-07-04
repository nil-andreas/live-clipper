package com.nilsson.sentiment.endpoint.api;

import com.nilsson.sentiment.domain.Clip;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class TwitchClipController {
    @NonNull
    private final TwitchClipService twitchClipService;

    @GetMapping("/clip/create/{userId}")
    public ResponseEntity<Mono<String>> createClip(@PathVariable String userId, OAuth2AuthenticationToken token) {
        log.debug("createClip for user {}", userId);
        return ResponseEntity.ok(twitchClipService.createClip(userId, token).map(Clip::getUrl));
    }

    @GetMapping("/clip")
    public String getStoredClips() {
        Stream<Clip> allClips = twitchClipService.findAllClips();
        String listEntries = allClips
                .sorted(Comparator.comparing(Clip::getTimestamp))
                .map(clip -> getHtmlListEntry().formatted(clip.getChannel(), clip.getUrl(), formatTimestamp(clip.getTimestamp())))
                .collect(Collectors.joining());
        return "<ul>" + listEntries + "</ul>";
    }

    private static String formatTimestamp(LocalDateTime timestamp) {
        return timestamp.truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private static String getHtmlListEntry() {
        return """
                <li>%s: <a href="%s" target="_blank">clip</a> %s</li>
                """;

    }
}
