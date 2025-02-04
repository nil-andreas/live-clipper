package com.nilsson.sentiment.message;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public interface MessageProcessingStrategy {

	Predicate<String> filter();
	Function<String, Map.Entry<Integer, String>> toMessage();
}
