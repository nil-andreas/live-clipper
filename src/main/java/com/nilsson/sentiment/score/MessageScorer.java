package com.nilsson.sentiment.score;

import com.nilsson.sentiment.PlotInput;
import com.nilsson.sentiment.domain.Message;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@AllArgsConstructor
@Component
public class MessageScorer {

    @NonNull
    private final PlotScoreStrategy scoreStrategy;

    public Flux<PlotInput> score(Flux<Message> chatStream) {
        return scoreStrategy.score(chatStream);
    }
}
