package com.mindscape.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/mock-analytics")
public class AnalyticsController {

    @GetMapping("/list")
    public Map<String, List<Map<String, Object>>> listAnalytics() {
        return Map.of(
            "qualAnalytics", List.of(
                Map.of("name", "CreativeOutput", "type", "text/plain")
            ),
            "quantAnalytics", List.of(
                Map.of("name", "ReasoningSteps", "type", "integer"),
                Map.of("name", "IdeasGenerated", "type", "integer")
            )
        );
    }

    @PostMapping
    public List<Map<String, Object>> getAnalytics(@RequestBody Map<String, Object> payload) {
        return List.of(
            Map.of(
                "inveniraStdID", "1001",
                "quantAnalytics", List.of(
                    Map.of("name", "ReasoningSteps", "type", "integer", "value", 12),
                    Map.of("name", "IdeasGenerated", "type", "integer", "value", 5)
                ),
                "qualAnalytics", List.of(
                    Map.of("name", "CreativeOutput", "type", "text/plain", "value", "Uma ideia surpreendente...")
                )
            )
        );
    }
}
