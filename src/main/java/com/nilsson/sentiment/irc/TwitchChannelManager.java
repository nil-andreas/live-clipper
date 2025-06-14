package com.nilsson.sentiment.irc;

import com.nilsson.sentiment.domain.AuthorizedClientKey;
import com.nilsson.sentiment.domain.ChannelSubscriptionEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@RequiredArgsConstructor
public class TwitchChannelManager {
    @NonNull
    private final TwitchIrc twitchIrc;
    @NonNull
    private final OAuth2AuthorizedClientService clientService;
    @NonNull
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public static final String PROPERTY_PREFIX = "twitch_channel_";

    private static final ConcurrentMap<String, Flux<String>> streams = new ConcurrentHashMap<>();

    public Flux<String> subscribe(String channel, OAuth2AuthenticationToken token) {
        if (streams.containsKey(channel)) {
            return streams.get(channel);
        }
        Flux<String> subscription = connectAndSubscribe(channel, token);
        ChannelSubscriptionEvent subscriptionEvent = createSubscriptionEvent(channel, token, subscription);
        storeSubscription(subscriptionEvent);
        return subscription;
    }

    private static ChannelSubscriptionEvent createSubscriptionEvent(String channel, OAuth2AuthenticationToken token, Flux<String> subscription) {
        return ChannelSubscriptionEvent.builder()
                .channel(channel)
                .stream(subscription)
                .authorizedClientKey(new AuthorizedClientKey(token))
                .build();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String channel, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(PROPERTY_PREFIX + channel, listener);
    }

    public void removePropertyListener(String channel, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(PROPERTY_PREFIX + channel, listener);
    }

    private void storeSubscription(ChannelSubscriptionEvent subscriptionEvent) {
        streams.put(subscriptionEvent.getChannel(), subscriptionEvent.getStream());
        propertyChangeSupport.firePropertyChange(PROPERTY_PREFIX + subscriptionEvent.getChannel(), null, subscriptionEvent);
    }

    private Flux<String> connectAndSubscribe(String channel, OAuth2AuthenticationToken token) {
        var client = clientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());
        var userName = getUserName(token);
        var oathToken = client.getAccessToken().getTokenValue();
        return twitchIrc.streamFromChannel(channel, userName, oathToken);
    }

    private String getUserName(OAuth2AuthenticationToken token) {
        token.getPrincipal();
        return token.getPrincipal().getAttribute("preferred_username");
    }

}
