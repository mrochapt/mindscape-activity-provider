package com.mindscape.activityprovider.analytics;

import com.mindscape.activityprovider.dto.AnalyticItem;
import com.mindscape.activityprovider.model.StudentAnalytics;

public interface AnalyticsCalculator {

    String getType(); // ex: "timeSpent", "timeOnTask"

    AnalyticItem calculate(String activityId,
                           String studentId,
                           StudentAnalytics studentAnalytics);
}
