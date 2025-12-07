package com.mindscape.activityprovider.analytics;

import com.mindscape.activityprovider.dto.AnalyticItem;
import com.mindscape.activityprovider.model.StudentAnalytics;

public class IdeasGeneratedCalculator implements AnalyticsCalculator {

    @Override
    public AnalyticItem calculate(StudentAnalytics sa) {
        return new AnalyticItem(
                "Ideas generated",
                "integer",
                sa.getIdeasGenerated()
        );
    }
}
