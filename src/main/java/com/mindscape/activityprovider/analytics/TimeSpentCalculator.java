package com.mindscape.activityprovider.analytics;

import com.mindscape.activityprovider.dto.AnalyticItem;
import com.mindscape.activityprovider.model.StudentAnalytics;

public class TimeSpentCalculator implements AnalyticsCalculator {

    @Override
    public String getType() {
        return "timeSpent";
    }

    @Override
    public AnalyticItem calculate(String activityId,
                                  String studentId,
                                  StudentAnalytics sa) {
        return new AnalyticItem(
            "TimeSpentSeconds",
            "integer",
            sa.getTimeSpentSeconds()
        );
    }
}
