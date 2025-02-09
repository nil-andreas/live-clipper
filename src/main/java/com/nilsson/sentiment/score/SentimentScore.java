package com.nilsson.sentiment.score;

import com.nilsson.sentiment.PlotInput;
import com.nilsson.sentiment.analyzer.AnalyzeResult;
import com.nilsson.sentiment.analyzer.TextAnalyzer;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;

@Component
@AllArgsConstructor
public class SentimentScore implements PlotScoreStrategy {
	@NonNull
	private final TextAnalyzer textAnalyzer;

	@Override
	public Flux<PlotInput> score(Flux<Map.Entry<Integer, String>> textStream) {
		return textStream.map(entry -> {
			AnalyzeResult sentiment = textAnalyzer.analyze(entry.getValue());
			return PlotInput.builder()
					.time(entry.getKey())
					.value(sentiment.value)
					.build();
		});
	}
}
