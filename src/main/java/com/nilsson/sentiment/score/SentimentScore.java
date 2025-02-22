package com.nilsson.sentiment.score;

import com.nilsson.sentiment.PlotInput;
import com.nilsson.sentiment.analyzer.AnalyzeResult;
import com.nilsson.sentiment.analyzer.TextAnalyzer;
import com.nilsson.sentiment.domain.Message;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@AllArgsConstructor
public class SentimentScore implements PlotScoreStrategy {
	@NonNull
	private final TextAnalyzer textAnalyzer;

	@Override
	public Flux<PlotInput> score(Flux<Message> textStream) {
		return textStream.map(entry -> {
			AnalyzeResult sentiment = textAnalyzer.analyze(entry.getText());
			return PlotInput.builder()
					.time(entry.getTimestamp())
					.value(sentiment.value)
					.build();
		});
	}
}
