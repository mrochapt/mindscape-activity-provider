package com.mindscape.activityprovider.controller;

import com.mindscape.activityprovider.analytics.AnalyticsFacade;
import com.mindscape.activityprovider.dto.AnalyticItem;
import com.mindscape.activityprovider.dto.AnalyticsResponseItem;
import com.mindscape.activityprovider.model.StudentAnalytics;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
public class MindscapeController {

    private final AnalyticsFacade analyticsFacade;

    // armazenamento simples em memória
    private final Map<String, StudentAnalytics> store = new ConcurrentHashMap<>();

    public MindscapeController(AnalyticsFacade analyticsFacade) {
        this.analyticsFacade = analyticsFacade;
    }

    @GetMapping("/")
    public String home() {
        return "MindScape Activity Provider is running.";
    }

    // Endpoint usado pela Inven!RA (exemplo)
    @PostMapping("/analytics")
    public AnalyticsResponseItem getAnalytics(@RequestBody Map<String, Object> payload) {
        String activityId = payload.get("activityID").toString();
        String studentId  = payload.get("inveniraStdID").toString();

        StudentAnalytics sa = findOrCreate(activityId, studentId);

        List<AnalyticItem> items =
            analyticsFacade.calculateAll(activityId, studentId, sa);

        return analyticsFacade.toResponse(studentId, items);
    }

    // Endpoint GET adicional para testes rápidos no browser:
    // /analytics-mindscape?activityID=...
    @GetMapping("analytics-mindscape")
    public List<AnalyticsResponseItem> getAnalyticsGet(@RequestParam("activityID") String activityId) {
        if (activityId == null || activityId.isBlank()) {
            throw new IllegalArgumentException("activityID obrigatório");
        }

        // filtra todos os StudentAnalytics desta atividade
        return store.values().stream()
            .filter(sa -> activityId.equals(sa.getActivityId()))
            .map(sa -> {
                String studentId = sa.getStudentId();
                List<AnalyticItem> items =
                    analyticsFacade.calculateAll(activityId, studentId, sa);
                return analyticsFacade.toResponse(studentId, items);
            })
            .collect(Collectors.toList());
    }

    private StudentAnalytics findOrCreate(String activityId, String studentId) {
        String key = activityId + "::" + studentId;
        return store.computeIfAbsent(key, k -> new StudentAnalytics(activityId, studentId));
    }
}
