package com.nilsson.sentiment.config;

import com.nilsson.sentiment.analyzer.TextAnalyzer;
import com.nilsson.sentiment.analyzer.nlp.NLPFactory;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

	@Bean
	public LayoutDialect layoutDialect() {
		return new LayoutDialect();
	}

	@Bean
	public TextAnalyzer textAnalyzer() {
		return NLPFactory.createNLP();
	}
}
