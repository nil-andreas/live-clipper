package com.nilsson.sentiment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.nilsson.sentiment.analyzer.AnalyzeResult;
import com.nilsson.sentiment.analyzer.SentimentType;
import com.nilsson.sentiment.analyzer.TextAnalyzer;
import com.nilsson.sentiment.message.MessageProcessingStrategy;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import reactor.core.publisher.Flux;

@AllArgsConstructor
@Service
public class TextAnalyzerService {
	@NonNull
	private final TextAnalyzer textAnalyzer;
	@NonNull
	private final MessageProcessingStrategy messageProcessing;

	public String analyzeSentence(String sentence, SentimentType parse) {
		AnalyzeResult result = textAnalyzer.analyze(sentence);
		return result.toString();
	}

	public Flux<PlotInput> fromStream() {
		var input = getClass().getResourceAsStream("/chat/twitch-chat-2371394480.csv");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
		return Flux.fromStream(bufferedReader.lines())
				.filter(messageProcessing.filter())
				.map(messageProcessing.toMessage())
				.map(toSentiment());
	}

	private Function<Map.Entry<Integer, String>, PlotInput> toSentiment() {
		return entry -> {
			AnalyzeResult sentiment = textAnalyzer.analyze(entry.getValue());
			return PlotInput.builder()
					.time(entry.getKey())
					.value(sentiment.value)
					.build();
		};
	}

}
