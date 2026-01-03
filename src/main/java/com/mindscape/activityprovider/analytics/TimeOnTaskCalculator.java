package com.mindscape.activityprovider.analytics;

import com.mindscape.activityprovider.dto.AnalyticItem;
import com.mindscape.activityprovider.model.StudentAnalytics;

public class TimeOnTaskCalculator implements AnalyticsCalculator {

    @Override
    public String getType() {
        return "timeOnTask";
    }

    @Override
    public AnalyticItem calculate(String activityId,
                                  String studentId,
                                  StudentAnalytics studentAnalytics) {
        double minutes = studentAnalytics.getTimeSpentSeconds() / 60.0;
        AnalyticItem item = new AnalyticItem();
        item.setName("TimeOnTaskMinutes");
        item.setType("quantitative");
        item.setValue(minutes);
        return item;
    }
}
