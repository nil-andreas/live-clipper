package com.nilsson.sentiment.message;

import com.nilsson.sentiment.domain.Message;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@AllArgsConstructor
@Component
public class MessageParser {
    @NonNull
    private final MessageParsingStrategy parsingStrategy;

    public Flux<Message> parse(Flux<String> chatStream) {
        return chatStream
                .filter(parsingStrategy.filter())
                .map(parsingStrategy.toMessage());
    }
}
