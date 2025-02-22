package com.nilsson.sentiment.highlight;

import com.nilsson.sentiment.irc.TwitchChannelManager;
import com.nilsson.sentiment.message.MessageParser;
import com.nilsson.sentiment.score.MessageScorer;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.nilsson.sentiment.highlight.HighlightCaptureTaskConfig.CHANNEL;

@Component
@RequiredArgsConstructor
@Slf4j
public class HighlightCaptureController implements PropertyChangeListener {
    @NonNull
    private final TwitchChannelManager twitchChannelManager;
    @NonNull
    private final MessageParser parser;
    @NonNull
    private final MessageScorer scorer;
    private ExecutorService virtualThreadExecutor;

    @PostConstruct
    public void init() {
        twitchChannelManager.addPropertyChangeListener(this);
        virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!evt.getPropertyName().startsWith(TwitchChannelManager.PROPERTY_PREFIX)) {
            // Only interested in twitch channels
            return;
        }
        if (evt.getNewValue() != null && evt.getNewValue() instanceof Flux<?> flux) {
            var highlightCaptureTask = createHighlightCaptureTaskForStream(evt, flux);
            twitchChannelManager.addPropertyChangeListener(evt.getPropertyName(), highlightCaptureTask);
            virtualThreadExecutor.submit(highlightCaptureTask);
        }

    }

    private HighlightCaptureTask createHighlightCaptureTaskForStream(PropertyChangeEvent evt, Flux<?> flux) {
        Flux<String> messageStream = flux.cast(String.class);
        return new HighlightCaptureTask(messageStream, parser, scorer, Map.of(CHANNEL, extractChannel(evt)));
    }

    private static String extractChannel(PropertyChangeEvent evt) {
        return evt.getPropertyName().substring(TwitchChannelManager.PROPERTY_PREFIX.length());
    }
}
