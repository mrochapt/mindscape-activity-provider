package com.mindscape.activityprovider.analytics;

import com.mindscape.activityprovider.dto.AnalyticItem;
import com.mindscape.activityprovider.model.StudentAnalytics;

public class ScoreCalculator implements AnalyticsCalculator {

    @Override
    public String getType() {
        return "score";
    }

    @Override
    public AnalyticItem calculate(String activityId,
                                  String studentId,
                                  StudentAnalytics studentAnalytics) {

        // Exemplo de heurística: ideias + bónus se houver reflexão
        int base = studentAnalytics.getIdeasGenerated();
        if (studentAnalytics.isReflectionSubmitted()) {
            base += 5;
        }

        AnalyticItem item = new AnalyticItem();
        item.setName("Score");
        item.setType("integer");
        item.setValue(base);

        return item;
    }
}
