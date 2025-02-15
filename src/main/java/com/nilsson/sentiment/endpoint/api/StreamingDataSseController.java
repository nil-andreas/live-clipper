package com.nilsson.sentiment.endpoint.api;

import com.nilsson.sentiment.PlotInput;
import com.nilsson.sentiment.TextAnalyzerService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@AllArgsConstructor
public class StreamingDataSseController {
    private final TextAnalyzerService textAnalyzerService;

    @GetMapping(path = "/streaming-data/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<PlotInput> serverSentEvents() {
        return textAnalyzerService.offlineStream();
    }

}
