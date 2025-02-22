package com.nilsson.sentiment.score;

import com.nilsson.sentiment.PlotInput;
import com.nilsson.sentiment.domain.Message;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicInteger;

@Primary
@Component
public class EngagementScore implements PlotScoreStrategy {

	@Override
	public Flux<PlotInput> score(Flux<Message> textStream) {
		final AtomicInteger counter = new AtomicInteger(0);
		final AtomicInteger currentKey = new AtomicInteger(-1);
		return textStream.handle((entry, sink) -> {
			if(currentKey.get() == -1) {
				currentKey.set(entry.getTimestamp());
				counter.set(1);
			} else if (entry.getTimestamp().equals(currentKey.get())) {
				counter.incrementAndGet();
			} else {
				sink.next(PlotInput.builder()
						.time(currentKey.get())
						.value(counter.get())
						.build());
				counter.set(1);
				currentKey.set(entry.getTimestamp());
			}
		});
	}
}
