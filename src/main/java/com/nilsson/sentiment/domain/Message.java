package com.nilsson.sentiment.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Message {
    Integer timestamp;
    String text;
}
