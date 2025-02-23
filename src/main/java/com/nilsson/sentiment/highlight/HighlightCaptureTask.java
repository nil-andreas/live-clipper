package com.nilsson.sentiment.highlight;

import com.nilsson.sentiment.PlotInput;
import com.nilsson.sentiment.domain.ChannelSubscriptionEvent;
import com.nilsson.sentiment.message.MessageParser;
import com.nilsson.sentiment.score.MessageScorer;
import com.nilsson.sentiment.service.TwitchClipService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class HighlightCaptureTask implements Runnable, PropertyChangeListener {
    private static final int CLIP_COOLDOWN = 20_000;
    @NonNull
    private final ChannelSubscriptionEvent subscription;
    @NonNull
    private final MessageParser parser;
    @NonNull
    private final MessageScorer scorer;
    @NonNull
    private final TwitchClipService twitchClipService;
    private long lastClipTimestamp = 0;
    private volatile boolean running = true;


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Any property changes means the stream is being changed. Stop this thread.
        log.info("PropertyChange event: {}. Set running=false.", evt);
        running = false;
    }

    @Override
    public void run() {
        log.info("Starting highlight capture task for channel {}", subscription.getChannel());
        var messageStream = parser.parse(subscription.getStream());
        Flux<Integer> scores = scorer.score(messageStream)
                .map(PlotInput::getValue)
                .take(Duration.of(300, ChronoUnit.SECONDS))
                .takeUntilOther(Flux.interval(Duration.ofMillis(100))
                        .filter(i -> !running)
                        .next());

        double aggregatedScore = Optional.ofNullable(scores.collectList()
                .map(scorer::aggregatedMessagesScore)
                .block()).orElse(0.0);
        log.info("AggregatedScore={} for channel={}", aggregatedScore, subscription.getChannel());
        if (aggregatedScore < 1 || !running) {
            log.info("Stopping highlight capture task for channel {}", subscription.getChannel());
            return;
        }
        scorer.score(messageStream)
                .takeUntilOther(Flux.interval(Duration.ofMillis(100))
                        .filter(t -> !running)
                        .next())
                .filter(s -> scorer.isInteresting(s.getValue(), aggregatedScore))
                .doOnNext(i -> log.info("Highlight score={} for channel={}", i, subscription.getChannel()))
                .flatMap(i -> clip())
                .doOnError(t -> log.error("Error while attempting to clip", t))
                .doOnNext(url -> log.info("Highlight url={} for channel={}", url, subscription.getChannel()))
                .subscribe();

    }

    private Mono<String> clip() {
        long clipTimestamp = System.currentTimeMillis();
        if (clipTimestamp - lastClipTimestamp < CLIP_COOLDOWN) {
            log.info("Will not create another clip within the clipCooldown of {} seconds for channel {}", CLIP_COOLDOWN / 1000.0, subscription.getChannel());
            return Mono.empty();
        }
        lastClipTimestamp = clipTimestamp;
        return twitchClipService.createClip(subscription.getChannel(), subscription.getAuthorizedClientKey());
    }

}
