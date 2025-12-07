package com.mindscape.activityprovider.analytics;

import com.mindscape.activityprovider.dto.AnalyticItem;
import com.mindscape.activityprovider.model.StudentAnalytics;

/**
 * Produto do padrão Factory Method.
 * Cada implementação calcula um analytic quantitativo a partir de um StudentAnalytics.
 */
public interface AnalyticsCalculator {

    AnalyticItem calculate(StudentAnalytics sa);
}
