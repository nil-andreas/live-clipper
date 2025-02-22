package com.nilsson.sentiment.message;

import com.nilsson.sentiment.domain.Message;

import java.util.function.Function;
import java.util.function.Predicate;

public interface MessageParsingStrategy {

	Predicate<String> filter();

	Function<String, Message> toMessage();
}
