package com.nilsson.sentiment.nlp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nilsson.sentiment.analyzer.AnalyzeResult;
import com.nilsson.sentiment.analyzer.nlp.NLP;
import com.nilsson.sentiment.analyzer.nlp.NLPFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class NLPTest {

	private NLP nlp;

	@BeforeEach
	void setUp() {
		nlp = NLPFactory.createNLP();
	}

	@Test
	void shouldReturnSentimentForSentence() {
		AnalyzeResult result = nlp.analyze("i am happy");
		assertThat(result).isEqualTo(AnalyzeResult.POSITIVE);
	}

	@Test
	void shouldReturnSentimentForLol() {
		AnalyzeResult sentiment = nlp.analyze("LOL");
		assertThat(sentiment).isEqualTo(AnalyzeResult.NEUTRAL);
	}

	@Test
	void shouldReturnSentimentForEmpty() {
		AnalyzeResult sentiment = nlp.analyze("   ");
		assertThat(sentiment).isEqualTo(AnalyzeResult.NEUTRAL);
	}

	@Test
	void shouldReturnSentimentForNegativeSentence() {
		AnalyzeResult sentiment = nlp.analyze("this movie is terrible");
		assertThat(sentiment).isEqualTo(AnalyzeResult.NEGATIVE);
	}
}