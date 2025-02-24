package com.nilsson.sentiment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Document(collection = "clips")
public class ClipEntity {
    @Id
    private String id;
    private String url;
    @Indexed(expireAfter = "24h")
    private LocalDateTime timestamp;
    private String channel;
}
