package com.nilsson.sentiment.endpoint.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClipManagerController {

    @GetMapping("clip-manager")
    public String clipManager() {
        return "clip-manager";
    }
}
