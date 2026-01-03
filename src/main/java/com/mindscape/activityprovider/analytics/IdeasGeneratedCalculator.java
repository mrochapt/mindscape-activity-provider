package com.mindscape.activityprovider.analytics;

import com.mindscape.activityprovider.dto.AnalyticItem;
import com.mindscape.activityprovider.model.StudentAnalytics;
import org.springframework.stereotype.Component;

@Component
public class IdeasGeneratedCalculator implements AnalyticsCalculator {

    @Override
    public String getType() {
        return "ideasGenerated";
    }

    @Override
    public AnalyticItem calculate(String activityId,
                                  String studentId,
                                  StudentAnalytics sa) {
        return new AnalyticItem(
            "Ideas generated",
            "integer",
            sa.getIdeasGenerated()
        );
    }
}
