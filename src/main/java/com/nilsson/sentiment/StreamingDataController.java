package com.nilsson.sentiment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nilsson.sentiment.analyzer.SentimentTypeParser;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

@Controller
@AllArgsConstructor
public class StreamingDataController {

    private final TextAnalyzerService textAnalyzerService;

    @GetMapping("/streaming-data")
    public String streamingData() {
        return "streaming-data";
    }

    @GetMapping(path = "/streaming-data/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<PlotInput> serverSentEvents(){
        return textAnalyzerService.fromStream();
    }

}
