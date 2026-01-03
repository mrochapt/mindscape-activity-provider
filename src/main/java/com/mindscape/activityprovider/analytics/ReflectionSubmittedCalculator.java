package com.mindscape.activityprovider.analytics;

import com.mindscape.activityprovider.dto.AnalyticItem;
import com.mindscape.activityprovider.model.StudentAnalytics;

public class ReflectionSubmittedCalculator implements AnalyticsCalculator {

    @Override
    public String getType() {
        return "reflectionSubmitted";
    }

    @Override
    public AnalyticItem calculate(String activityId,
                                  String studentId,
                                  StudentAnalytics sa) {
        return new AnalyticItem(
            "Reflection submitted",
            "boolean",
            sa.isReflectionSubmitted()
        );
    }
}
