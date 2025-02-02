package com.nilsson.sentiment.analyzer;

public class SentimentTypeParser {
    public static SentimentType parse(String type) {
        return switch (type) {
            case "stanford-sentiment" -> SentimentType.STANFORD_SENTIMENT;
            default -> throw new IllegalArgumentException("Unknown sentiment type: " + type);
        };
    }
}
