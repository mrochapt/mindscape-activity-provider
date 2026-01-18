package com.mindscape.activityprovider.analytics;

import com.mindscape.activityprovider.dto.AnalyticItem;
import com.mindscape.activityprovider.dto.AnalyticsResponseItem;
import com.mindscape.activityprovider.model.StudentAnalytics;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AnalyticsFacade {

    private final List<AnalyticsCalculator> calculators;
    private final Map<String, AnalyticsCalculator> strategyMap;
    
    public AnalyticsFacade(List<AnalyticsCalculator> calculators) {
        this.calculators = calculators;
        this.strategyMap = calculators.stream()
            .collect(Collectors.toMap(
                AnalyticsCalculator::getType,
                calc -> calc,
                (existing, replacement) -> existing
            ));
    }

    public List<AnalyticItem> calculateAll(String activityId,
                                           String studentId,
                                           StudentAnalytics sa) {
        List<AnalyticItem> result = new ArrayList<>();
        for (AnalyticsCalculator calc : calculators) {
            result.add(calc.calculate(activityId, studentId, sa));
        }
        return result;
    }
    
    public List<AnalyticItem> calculateMetrics(String activityId, String studentId, StudentAnalytics sa, List<String> metricTypes) {
        return metricTypes.stream()
            .map(strategyMap::get)
            .filter(Objects::nonNull)
            .map(calc -> calc.calculate(activityId, studentId, sa))
            .toList();
    }

    public AnalyticsCalculator getStrategyFor(String metricType) {
        return strategyMap.get(metricType);
    }

    public AnalyticsResponseItem toResponse(String studentId,
                                            List<AnalyticItem> items) {
        List<AnalyticItem> quant = new ArrayList<>();
        List<AnalyticItem> qual  = new ArrayList<>();

        for (AnalyticItem item : items) {
            if ("text/plain".equals(item.getType())) {
                qual.add(item);
            } else {
                quant.add(item);
            }
        }

        return new AnalyticsResponseItem(studentId, quant, qual);
    }
}
