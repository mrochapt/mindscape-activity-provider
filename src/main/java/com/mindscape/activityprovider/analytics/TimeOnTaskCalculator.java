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

        double minutes = studentAnalytics.getTotalTimeMinutes();

        AnalyticItem item = new AnalyticItem();
        item.setId("timeOnTask");
        item.setLabel("Tempo em tarefa (min)");
        item.setValue(minutes);
        item.setType("quantitative");
        return item;
    }
}
