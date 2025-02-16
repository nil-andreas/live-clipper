package com.nilsson.sentiment.score;

import com.nilsson.sentiment.PlotInput;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Primary
@Component
public class EngagementScore implements PlotScoreStrategy {

	@Override
	public Flux<PlotInput> score(Flux<Map.Entry<Integer, String>> textStream) {
		final AtomicInteger counter = new AtomicInteger(0);
		final AtomicInteger currentKey = new AtomicInteger(-1);
		return textStream.handle((entry, sink) -> {
			if(currentKey.get() == -1) {
				currentKey.set(entry.getKey());
				counter.set(1);
			} else if (entry.getKey().equals(currentKey.get())) {
				counter.incrementAndGet();
			} else {
				sink.next(PlotInput.builder()
						.time(currentKey.get())
						.value(counter.get())
						.build());
				counter.set(1);
				currentKey.set(entry.getKey());
			}
		});
	}
}
