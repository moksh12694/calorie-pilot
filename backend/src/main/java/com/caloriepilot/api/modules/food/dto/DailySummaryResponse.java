package com.caloriepilot.api.modules.food.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DailySummaryResponse(
        LocalDate date,
        BigDecimal calories,
        BigDecimal proteinG,
        BigDecimal carbsG,
        BigDecimal fatG,
        Integer calorieGoal,
        Integer proteinGoalG,
        Integer carbsGoalG,
        Integer fatGoalG,
        List<MealEntryResponse> entries
) {}
