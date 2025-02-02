package com.nilsson.sentiment.analyzer.nlp;

import static com.nilsson.sentiment.analyzer.nlp.AnalysisResultParser.*;

import com.nilsson.sentiment.analyzer.AnalyzeResult;
import com.nilsson.sentiment.analyzer.TextAnalyzer;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NLP implements TextAnalyzer {
	private final StanfordCoreNLP pipeline;

	public NLP(@NonNull StanfordCoreNLP pipeline) {
		this.pipeline = pipeline;
	}

	@Override
	public AnalyzeResult analyze(String text) {
		int sentimentInt;
		String sentimentName = "Neutral";
		Annotation annotation = pipeline.process(text);
		for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
			Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
			sentimentInt = RNNCoreAnnotations.getPredictedClass(tree);
			sentimentName = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
			log.debug(sentimentName + "\t" + sentimentInt + "\t" + sentence);
			if (!sentimentName.equals("Neutral")) {
				return mapToAnalyzationResult(sentimentName);
			}
		}
		return mapToAnalyzationResult(sentimentName);
	}

}
