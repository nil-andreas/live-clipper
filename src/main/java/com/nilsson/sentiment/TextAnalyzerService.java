package com.nilsson.sentiment;

import com.nilsson.sentiment.analyzer.AnalyzeResult;
import com.nilsson.sentiment.analyzer.SentimentType;
import com.nilsson.sentiment.analyzer.TextAnalyzer;
import com.nilsson.sentiment.message.MessageProcessingStrategy;
import com.nilsson.sentiment.score.PlotScoreStrategy;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;

@AllArgsConstructor
@Service
public class TextAnalyzerService {
	@NonNull
	private final TextAnalyzer textAnalyzer;
	@NonNull
	private final MessageProcessingStrategy messageProcessing;
	@NonNull
	private final PlotScoreStrategy plotScoreStrategy;

	public String analyzeSentence(String sentence, SentimentType parse) {
		AnalyzeResult result = textAnalyzer.analyze(sentence);
		return result.toString();
	}

	public Flux<PlotInput> fromStream() {
		var input = getClass().getResourceAsStream("/chat/twitch-chat-2371394480.csv");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
		return plotScoreStrategy.score(Flux.fromStream(bufferedReader.lines())
				.filter(messageProcessing.filter())
						.map(messageProcessing.toMessage()))
				.delayElements(Duration.ofMillis(200));
	}

}
