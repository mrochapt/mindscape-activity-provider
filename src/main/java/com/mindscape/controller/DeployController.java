package com.mindscape.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/deploy")
public class DeployController {

    @PostMapping
    public Map<String, String> deploy(@RequestBody Map<String, Object> payload) {

        String activityId = payload.get("activityID").toString();
        String userId = payload.get("Inven!RAstdID").toString();

        return Map.of(
            "launch_url", "https://mindscape.example.com/launch?act=" + activityId + "&user=" + userId
        );
    }
}
