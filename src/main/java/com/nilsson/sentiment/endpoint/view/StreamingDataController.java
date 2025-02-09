package com.nilsson.sentiment.endpoint.view;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class StreamingDataController {

    @GetMapping("/streaming-data")
    public String streamingData() {
        return "streaming-data";
    }

}
