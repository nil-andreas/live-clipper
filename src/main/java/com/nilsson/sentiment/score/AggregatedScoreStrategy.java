package com.nilsson.sentiment.score;

import java.util.List;

public interface AggregatedScoreStrategy {
    Double score(List<Integer> messages);

    boolean isInteresting(double currentScore, double aggregatedScore);
}
