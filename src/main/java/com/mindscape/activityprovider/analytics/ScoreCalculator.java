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

       
        double score = studentAnalytics.getScore();

        AnalyticItem item = new AnalyticItem();
        item.setId("score");
        item.setLabel("Pontuação final");
        item.setValue(score);
        item.setType("quantitative");
        return item;
    }
}
