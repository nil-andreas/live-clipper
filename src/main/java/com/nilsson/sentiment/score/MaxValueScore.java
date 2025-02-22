package com.nilsson.sentiment.score;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class MaxValueScore implements AggregatedScoreStrategy {
    @Override
    public Double score(List<Integer> messages) {
        return messages.stream()
                .max(Integer::compareTo)
                .map(Integer::doubleValue)
                .orElse(0.0);
    }

    @Override
    public boolean isInteresting(double currentScore, double aggregatedScore) {
        return currentScore > 1.2 * aggregatedScore;
    }


}
