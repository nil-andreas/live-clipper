package com.nilsson.sentiment.highlight;

import com.nilsson.sentiment.domain.ChannelSubscriptionEvent;
import com.nilsson.sentiment.irc.TwitchChannelManager;
import com.nilsson.sentiment.message.MessageParser;
import com.nilsson.sentiment.score.MessageScorer;
import com.nilsson.sentiment.service.TwitchClipService;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    @NonNull
    private final TwitchClipService twitchClipService;
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
        if (evt.getNewValue() != null && evt.getNewValue() instanceof ChannelSubscriptionEvent event) {
            var highlightCaptureTask = createHighlightCaptureTaskForStream(event);
            twitchChannelManager.addPropertyChangeListener(evt.getPropertyName(), highlightCaptureTask);
            virtualThreadExecutor.submit(highlightCaptureTask);
        }

    }

    private HighlightCaptureTask createHighlightCaptureTaskForStream(ChannelSubscriptionEvent event) {
        return new HighlightCaptureTask(event, parser, scorer, twitchClipService);
    }

}
