package com.nilsson.sentiment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
@RedisHash(value = "ClipEntity", timeToLive = 24 * 60 * 60)
public class ClipEntity {
    @Id
    private String id;
    private String url;
    private LocalDateTime timestamp;
    private String channel;
}
