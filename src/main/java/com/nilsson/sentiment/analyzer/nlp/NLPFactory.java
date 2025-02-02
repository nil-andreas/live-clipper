package com.nilsson.sentiment.analyzer.nlp;

import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class NLPFactory {
	private NLPFactory() {}

	public static SentimentEngine createNLP() {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		StanfordCoreNLP stanfordCoreNLP = new StanfordCoreNLP(props);
		return new SentimentEngine(stanfordCoreNLP);
	}

}
