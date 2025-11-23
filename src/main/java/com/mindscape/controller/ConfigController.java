package com.mindscape.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @GetMapping
    public String getConfigPage() {
        return "<html><body><h2>MindScape Activity Configuration</h2>" +
                "<form>" +
                "<label>Prompt criativo:</label><input id='prompt' name='prompt'><br>" +
                "<label>Nível de liberdade:</label><input id='freedom' name='freedom'><br>" +
                "</form></body></html>";
    }

    @GetMapping("/params")
    public List<Map<String, String>> getParams() {
        return List.of(
                Map.of("name", "prompt", "type", "text/plain"),
                Map.of("name", "freedom", "type", "text/plain")
        );
    }
}
