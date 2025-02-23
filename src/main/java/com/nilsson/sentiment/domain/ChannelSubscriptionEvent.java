package com.nilsson.sentiment.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import reactor.core.publisher.Flux;

@Value
@Builder
public class ChannelSubscriptionEvent {
    @NonNull
    Flux<String> stream;
    @NonNull
    String channel;
    @NonNull
    AuthorizedClientKey authorizedClientKey;
}
