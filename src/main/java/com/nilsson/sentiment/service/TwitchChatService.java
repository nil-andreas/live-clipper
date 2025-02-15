package com.nilsson.sentiment.service;

import com.nilsson.sentiment.PlotInput;
import com.nilsson.sentiment.TextAnalyzerService;
import com.nilsson.sentiment.irc.TwitchIrc;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@AllArgsConstructor
@Service
public class TwitchChatService {
    @NonNull
    private final TwitchIrc twitchIrc;
    @NonNull
    private final OAuth2AuthorizedClientService clientService;
    @NonNull
    private final TextAnalyzerService textAnalyzerService;

    public Flux<PlotInput> streamFromChannel(String channel, OAuth2AuthenticationToken token) {
        var client = clientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());
        var userName = getUserName(token);
        var oathToken = client.getAccessToken().getTokenValue();
        Flux<String> chatStream = twitchIrc.streamFromChannel(channel, userName, oathToken);
        return textAnalyzerService.fromStream(chatStream);
    }

    private String getUserName(OAuth2AuthenticationToken token) {
        token.getPrincipal();
        return token.getPrincipal().getAttribute("preferred_username");
    }
}
