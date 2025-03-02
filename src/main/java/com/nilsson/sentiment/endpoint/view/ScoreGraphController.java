package com.nilsson.sentiment.endpoint.view;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class ScoreGraphController {

    @GetMapping("/secured/score-graph")
    public String streamingData() {
        return "secured/score-graph";
    }

}
