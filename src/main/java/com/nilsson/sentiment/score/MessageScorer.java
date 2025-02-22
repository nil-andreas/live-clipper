package com.nilsson.sentiment.score;

import com.nilsson.sentiment.PlotInput;
import com.nilsson.sentiment.domain.Message;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@AllArgsConstructor
@Component
public class MessageScorer {

    @NonNull
    private final PlotScoreStrategy scoreStrategy;
    @NonNull
    private final AggregatedScoreStrategy aggregatedScoreStrategy;

    public Flux<PlotInput> score(Flux<Message> chatStream) {
        return scoreStrategy.score(chatStream);
    }

    public Double aggregatedMessagesScore(List<Integer> messages) {
        return aggregatedScoreStrategy.score(messages);
    }

    public boolean isInteresting(double currentScore, double aggregatedScore) {
        return aggregatedScoreStrategy.isInteresting(currentScore, aggregatedScore);
    }
}
