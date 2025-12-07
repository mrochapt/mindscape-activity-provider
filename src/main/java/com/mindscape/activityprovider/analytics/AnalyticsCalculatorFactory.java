package com.mindscape.activityprovider.analytics;

/**
 * Implementa o padrão Factory Method:
 * encapsula a lógica de criação de calculadores de analytics.
 */
public class AnalyticsCalculatorFactory {

    public AnalyticsCalculator create(String metricKey) {
        return switch (metricKey) {
            case "ideasGenerated"      -> new IdeasGeneratedCalculator();
            case "timeSpent"           -> new TimeSpentCalculator();
            case "reflectionSubmitted" -> new ReflectionSubmittedCalculator();
            default -> throw new IllegalArgumentException("Unknown metric key: " + metricKey);
        };
    }
}
