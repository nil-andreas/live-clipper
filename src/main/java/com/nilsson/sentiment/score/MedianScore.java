package com.nilsson.sentiment.score;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class MedianScore implements AggregatedScoreStrategy {
    @Override
    public Double score(List<Integer> messages) {
        if (messages.isEmpty()) {
            return 0.0;
        }
        Collections.sort(messages);
        if (messages.size() % 2 != 0) {
            // messages size is odd. Median is the middle value
            return (double) messages.get(messages.size() / 2);
        }
        // messages size is even. Median is the avg of the two middle elems
        return (messages.get(messages.size() / 2 - 1) + messages.get(messages.size() / 2)) / 2.0;
    }

    @Override
    public boolean isInteresting(double currentScore, double aggregatedScore) {
        return currentScore > 2.5 * aggregatedScore;
    }
}
