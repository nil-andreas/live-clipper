package com.nilsson.sentiment.analyzer.nlp;

import com.nilsson.sentiment.analyzer.AnalyzeResult;

public class AnalysisResultParser {
	private AnalysisResultParser() {}

	public static AnalyzeResult mapToAnalyzationResult(String sentimentName) {
		return switch (sentimentName) {
			case "Very negative", "Negative" -> AnalyzeResult.NEGATIVE;
			case "Positive", "Very positive" -> AnalyzeResult.POSITIVE;
			case "Neutral" -> AnalyzeResult.NEUTRAL;
			default -> throw new IllegalArgumentException("Unknown sentiment: " + sentimentName);
		};
	}
}
