package com.nilsson.sentiment.domain;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class Clip {
    String url;
    LocalDateTime timestamp;
    String channel;
}
