package com.nilsson.sentiment.score;

import com.nilsson.sentiment.PlotInput;
import com.nilsson.sentiment.domain.Message;
import reactor.core.publisher.Flux;

public interface PlotScoreStrategy {
	Flux<PlotInput> score(Flux<Message> textStream);
}
