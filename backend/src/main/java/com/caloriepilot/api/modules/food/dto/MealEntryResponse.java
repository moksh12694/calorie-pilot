package com.caloriepilot.api.modules.food.dto;

import com.caloriepilot.api.modules.food.MealType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MealEntryResponse(
        Long id,
        LocalDate date,
        MealType meal,
        Long foodId,
        String foodName,
        BigDecimal servings,
        BigDecimal calories,
        BigDecimal proteinG,
        BigDecimal carbsG,
        BigDecimal fatG
) {}
