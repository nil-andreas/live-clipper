package com.nilsson.sentiment.highlight;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "twitch.clip")
@Getter
@Setter
public class TwitchClipProperties {
    private long cooldownTimeInMillis = 60_000;
    private long scoringCalibrationTimeInMillis = 30_000;
}
