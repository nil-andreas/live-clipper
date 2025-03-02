package com.nilsson.sentiment.endpoint.api;

import com.nilsson.sentiment.PlotInput;
import com.nilsson.sentiment.service.TwitchChatService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@AllArgsConstructor
@RequestMapping("/secured/api")
public class TwitchChatStreamingController {
    @NonNull
    private final TwitchChatService twitchChatService;

    @GetMapping(path = "/streaming-data/twitch-chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<PlotInput> serverSentEvents(@RequestParam String channel, OAuth2AuthenticationToken token) {
        return twitchChatService.streamFromChannel(channel, token);
    }
}
