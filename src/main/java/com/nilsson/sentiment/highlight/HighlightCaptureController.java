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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
    @NonNull
    private final TwitchClipProperties twitchClipProperties;
    private final List<TaskInfo> runningTasks = Collections.synchronizedList(new ArrayList<>());
    private ExecutorService virtualThreadExecutor;


    @PostConstruct
    public void init() {
        twitchChannelManager.addPropertyChangeListener(this);
        virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    }

    @Scheduled(fixedRate = 5000)
    public void unsubscribe() {
        runningTasks.stream()
                .filter(taskInfo -> taskInfo.future.isDone())
                .peek(taskInfo -> log.debug("Unsubscribe capture task for channel={}", taskInfo.channel))
                .forEach(taskInfo -> twitchChannelManager.removePropertyListener(taskInfo.channel(), taskInfo.listener()));

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
            Future<?> future = virtualThreadExecutor.submit(highlightCaptureTask);
            runningTasks.add(new TaskInfo(event.getChannel(), highlightCaptureTask, future));
        }

    }

    private HighlightCaptureTask createHighlightCaptureTaskForStream(ChannelSubscriptionEvent event) {
        return new HighlightCaptureTask(event, parser, scorer, twitchClipService, twitchClipProperties);
    }

    record TaskInfo(String channel, PropertyChangeListener listener, Future<?> future) {
    }
}
