package com.nilsson.sentiment.highlight;

import com.nilsson.sentiment.PlotInput;
import com.nilsson.sentiment.message.MessageParser;
import com.nilsson.sentiment.score.MessageScorer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

import static com.nilsson.sentiment.highlight.HighlightCaptureTaskConfig.CHANNEL;

@RequiredArgsConstructor
@Slf4j
public class HighlightCaptureTask implements Runnable, PropertyChangeListener {
    @NonNull
    private final Flux<String> stream;
    @NonNull
    private final MessageParser parser;
    @NonNull
    private final MessageScorer scorer;
    @NonNull
    private final Map<HighlightCaptureTaskConfig, String> config;
    private volatile boolean running = true;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Any property changes means the stream is being changed. Stop this thread.
        log.info("PropertyChange event: {}. Set running=false.", evt);
        running = false;
    }

    @Override
    public void run() {
        if (!config.containsKey(CHANNEL)) {
            log.warn("No channel configured. Exiting thread.");
            return;
        }
        log.info("Starting highlight capture task for channel {}", config.get(CHANNEL));
        var messageStream = parser.parse(stream);
        Flux<Integer> scores = scorer.score(messageStream)
                .map(PlotInput::getValue)
                .take(Duration.of(30, ChronoUnit.SECONDS))
                .takeUntilOther(Flux.interval(Duration.ofMillis(100))
                        .filter(i -> !running)
                        .next());

        double aggregatedScore = Optional.ofNullable(scores.collectList()
                .map(scorer::aggregatedMessagesScore)
                .block()).orElse(0.0);
        log.info("AggregatedScore={} for channel={}", aggregatedScore, config.get(CHANNEL));
        if (aggregatedScore < 1 || !running) {
            log.info("Stopping highlight capture task for channel {}", config.get(CHANNEL));
            return;
        }
        scorer.score(messageStream)
                .takeUntilOther(Flux.interval(Duration.ofMillis(100))
                        .filter(t -> !running)
                        .next())
                .filter(s -> scorer.isInteresting(s.getValue(), aggregatedScore))
                .doOnNext(i -> log.info("Highlight score={} for channel={}", i, config.get(CHANNEL)))
                .subscribe();

    }

}
