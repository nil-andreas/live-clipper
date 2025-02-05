package com.nilsson.sentiment.score;

import java.util.Map;

import com.nilsson.sentiment.PlotInput;

import reactor.core.publisher.Flux;

public interface PlotScoreStrategy {
	Flux<PlotInput> score(Flux<Map.Entry<Integer, String>> textStream);
}
