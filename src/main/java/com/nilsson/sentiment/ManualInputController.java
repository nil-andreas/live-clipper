package com.nilsson.sentiment;

import com.nilsson.sentiment.analyzer.SentimentTypeParser;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor
public class ManualInputController {

    private final TextAnalyzerService textAnalyzerService;

    @GetMapping("/manual-input")
    public String manualInput() {
        return "manual-input";
    }

    @PostMapping(value = "/manual-input", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String manualInput(@RequestParam String sentence, @RequestParam String sentimentType) {
        return textAnalyzerService.analyzeSentence(sentence, SentimentTypeParser.parse(sentimentType));

    }

}
