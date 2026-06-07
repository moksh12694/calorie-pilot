package com.caloriepilot.api.modules.analytics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record AnalyticsRange(
        LocalDate from,
        LocalDate to,
        Totals totals,
        Averages averages,
        Series series
) {
    public record Totals(
            Long stepsTotal,
            BigDecimal caloriesTotal,
            Long waterMlTotal
    ) {}

    public record Averages(
            BigDecimal avgSteps,
            BigDecimal avgCalories,
            BigDecimal avgWaterMl,
            BigDecimal avgWeightKg
    ) {}

    public record Series(
            List<Point> steps,
            List<Point> calories,
            List<Point> water,
            List<Point> weight
    ) {}
}
