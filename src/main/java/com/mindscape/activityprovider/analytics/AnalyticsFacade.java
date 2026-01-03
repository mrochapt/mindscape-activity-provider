package com.mindscape.activityprovider.analytics;

import com.mindscape.activityprovider.dto.AnalyticItem;
import com.mindscape.activityprovider.dto.AnalyticsResponseItem;
import com.mindscape.activityprovider.model.StudentAnalytics;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsFacade {

    private final List<AnalyticsCalculator> calculators;

    public AnalyticsFacade(List<AnalyticsCalculator> calculators) {
        this.calculators = calculators;
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
