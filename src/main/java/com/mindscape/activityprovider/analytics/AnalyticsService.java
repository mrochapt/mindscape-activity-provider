package com.mindscape.activityprovider.analytics;

import com.mindscape.activityprovider.dto.AnalyticItem;
import com.mindscape.activityprovider.dto.AnalyticsResponseItem;
import com.mindscape.activityprovider.model.StudentAnalytics;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serviço que gere o armazenamento de analytics e
 * usa o Factory Method para calcular métricas quantitativas.
 */
@Service
public class AnalyticsService {

    // activityId -> (studentId -> analytics)
    private final Map<String, Map<String, StudentAnalytics>> analyticsStore =
            new ConcurrentHashMap<>();

    private final AnalyticsCalculatorFactory factory = new AnalyticsCalculatorFactory();

    // ------- gestão de StudentAnalytics (substitui o Map no controller) -------

    public void ensureActivityExists(String activityId) {
        analyticsStore.computeIfAbsent(activityId, k -> new ConcurrentHashMap<>());
    }

    public StudentAnalytics getOrCreateStudentAnalytics(String activityId, String studentId) {
        Map<String, StudentAnalytics> perActivity =
                analyticsStore.computeIfAbsent(activityId, k -> new ConcurrentHashMap<>());

        return perActivity.computeIfAbsent(studentId,
                sid -> new StudentAnalytics(activityId, sid));
    }

    // ------- cálculo de analytics (usado em /analytics-mindscape) -------

    public List<AnalyticsResponseItem> getAnalyticsForActivity(String activityId) {
        Map<String, StudentAnalytics> perActivity =
                analyticsStore.getOrDefault(activityId, Map.of());

        List<AnalyticsResponseItem> response = new ArrayList<>();

        for (StudentAnalytics sa : perActivity.values()) {
            // Quantitativos via Factory Method
            List<AnalyticItem> quant = computeQuantitativeAnalytics(sa);

            // Qualitativos de forma direta
            List<AnalyticItem> qual = new ArrayList<>();
            qual.add(new AnalyticItem("Prompt used", "text/plain", sa.getPrompt()));
            qual.add(new AnalyticItem("Ideas text", "text/plain", sa.getIdeasText()));

            AnalyticsResponseItem item =
                    new AnalyticsResponseItem(sa.getStudentId(), quant, qual);

            response.add(item);
        }

        return response;
    }

    // Usa Factory Method internamente
    private List<AnalyticItem> computeQuantitativeAnalytics(StudentAnalytics sa) {
        List<String> metricKeys = List.of(
                "ideasGenerated",
                "timeSpent",
                "reflectionSubmitted"
        );

        List<AnalyticItem> result = new ArrayList<>();

        for (String key : metricKeys) {
            AnalyticsCalculator calculator = factory.create(key);
            AnalyticItem item = calculator.calculate(sa);
            result.add(item);
        }

        return result;
    }
}
