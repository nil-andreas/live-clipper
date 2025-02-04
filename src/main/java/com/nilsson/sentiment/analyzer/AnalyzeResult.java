package com.nilsson.sentiment.analyzer;

public enum AnalyzeResult {
	POSITIVE(1), NEGATIVE(-1), NEUTRAL(0);

	public final int value;

	AnalyzeResult(int value) {
		this.value = value;
	}
}
