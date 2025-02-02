package com.nilsson.sentiment;

import com.nilsson.sentiment.analyzer.AnalyzeResult;
import com.nilsson.sentiment.analyzer.SentimentType;
import com.nilsson.sentiment.analyzer.TextAnalyzer;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TextAnalyzerService {
    @NonNull
    private final TextAnalyzer textAnalyzer;


    public String analyzeSentence(String sentence, SentimentType parse) {
        AnalyzeResult result = textAnalyzer.analyze(sentence);
        return result.toString();
    }
}
